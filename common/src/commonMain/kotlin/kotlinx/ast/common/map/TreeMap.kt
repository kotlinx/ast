package kotlinx.ast.common.map

import kotlinx.ast.common.AstFailure
import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstWithAttachments
import kotlinx.ast.common.ast.AstWithRawAst
import kotlinx.ast.common.ast.astAttachmentsOrNull
import kotlinx.ast.common.filter.TreeFilterByDescription
import kotlinx.ast.common.klass.RawAst
import kotlinx.ast.common.util.AssemblyLine
import kotlinx.ast.common.util.add

internal fun <State> treeMap(
    state: State,
    mapper: List<TreeMapper<State>>,
    ast: List<Ast>, attachRawAst: Boolean
): AstResult<State, List<Ast>> {
    return TreeMap(state, mapper, ast, attachRawAst).treeMap()
}

private data class AstWrapper(val index: Int, val ast: Ast)

private fun List<Ast>.wrapWithIndex(offset: Int = 0): List<AstWrapper> {
    return mapIndexed { index, a ->
        AstWrapper(index + offset, a)
    }
}

class DefaultTreeMapResultFactory<State>(
    state: State
) : TreeMapResultFactory<State> by TreeMap(
    state = state,
    mapper = emptyList(),
    ast = emptyList(),
    attachRawAst = false
)

private data class TreeMap<State>(
    val state: State,
    val mapper: AssemblyLine<TreeMapper<State>>,
    val ast: AssemblyLine<AstWrapper>,
    val nextAstId: Int,
    val attachRawAst: Boolean,
    val skip: Map<AstWrapper, Set<TreeMapper<State>>> = emptyMap(),
    val done: Boolean = false
) : TreeMapContext<State>() {
    constructor(
        state: State,
        mapper: List<TreeMapper<State>>,
        ast: List<Ast>,
        attachRawAst: Boolean
    ) : this(
        state = state,
        mapper = AssemblyLine(mapper),
        ast = AssemblyLine(ast.wrapWithIndex()),
        nextAstId = ast.size,
        attachRawAst = attachRawAst
    )

    val currentAst: Ast? = ast.waiting.firstOrNull()?.ast

    fun treeMap(): AstResult<State, List<Ast>> {
        return treeMapLoop(this).flatMap { result ->
            astSuccess(result.ast.processed.map(AstWrapper::ast))
        }
    }

    override fun recursive(
        nodes: List<Ast>,
    ): AstResult<State, List<Ast>> {
        return copy(
            skip = emptyMap(),
            mapper = mapper.reset(),
            ast = AssemblyLine(waiting = nodes.wrapWithIndex()),
            nextAstId = nodes.size
        ).treeMap()
    }

    override fun List<Ast>.astFold(
        mapper: TreeMapContext<State>.(Ast) -> AstResult<State, List<Ast>>
    ): AstResult<State, List<Ast>> {
        return fold(astContinue(emptyList())) { result, ast ->
            when (result) {
                is AstSuccess<State, List<Ast>> ->
                    this@TreeMap(result).mapper(ast).map { list ->
                        result.success + list
                    }
                else ->
                    result
            }
        }
    }

    private operator fun invoke(astResult: AstResult<State, List<Ast>>): TreeMap<State> {
        // TODO apply state
        return this
    }

    override fun <T> astContinue(result: List<T>): AstResult<State, List<T>> {
        return astSuccess(result)
    }

    override fun astKeep(): AstResult<State, List<Ast>> {
        return if (currentAst == null) {
            "astKeep() without currentAst!".astError()
        } else {
            astSuccess(listOf(currentAst))
        }
    }

    override fun astDrop(): AstResult<State, List<Ast>> {
        return astSuccess(emptyList())
    }

    override fun <T> List<String>.astError(): AstResult<State, T> {
        return state.astFailure(this)
    }

    override fun <T> astSuccess(result: T): AstResult<State, T> {
        return DefaultAstSuccess(state, this, result)
    }

    override fun <Other, T> Other.astSuccess(result: T): AstResult<Other, T> {
        return DefaultAstSuccess(this, DefaultTreeMapResultFactory(this), result)
    }

    override fun <T> astFailure(errors: List<String>): AstResult<State, T> {
        return DefaultAstFailure(state, this, errors)
    }

    override fun <Other, T> Other.astFailure(errors: List<String>): AstResult<Other, T> {
        return DefaultAstFailure(this, DefaultTreeMapResultFactory(this), errors)
    }
}

private tailrec fun <State> treeMapLoop(treeMap: TreeMap<State>): AstResult<State, TreeMap<State>> {
    val result = treeMap.treeMapInternal()
    return when {
        result is AstSuccess && !result.success.done ->
            treeMapLoop(result.success)
        else ->
            result
    }
}

private fun <State> TreeMap<State>.treeMapInternal(): AstResult<State, TreeMap<State>> {
    return when {
        done ->
            astSuccess(this)

        ast.isEmpty() ->
            astSuccess(copy(done = true))

        mapper.isEmpty() ->
            astSuccess(copy(mapper = mapper.reset(), ast = ast.next()))

        else -> {
            val m: TreeMapper<State> = mapper.waiting.first()
            val w: AstWrapper = ast.waiting.first()
            val a: Ast = w.ast
            val filter = m.filter
            val ignore = filter is TreeFilterByDescription && !filter.descriptions.contains(a.description)
            if (ignore || skip[w]?.contains(m) == true) {
                astSuccess(copy(mapper = mapper.next()))
            } else {
                copy(skip = skip.add(w, m)).treeMapInternal(m, a)
            }
        }
    }
}

private fun <State> TreeMap<State>.treeMapInternal(
    m: TreeMapper<State>,
    a: Ast
): AstResult<State, TreeMap<State>> {
    return if (m.filter.matches(a)) {
        val context: TreeMapContext<State> = copy(
            ast = AssemblyLine(waiting = listOf(a).wrapWithIndex())
        )
        val attachments = a.astAttachmentsOrNull
        when (val result = m.converter.convert(context, a)) {
            null ->
                astSuccess(copy(mapper = mapper.next()))
            is AstSuccess<State, *> -> {
                val success = result.success
                when {
                    success is List<*> && success.size == 1 && success[0] == a ->
                        astSuccess(
                            copy(
                                mapper = mapper.next()
                            )
                        )
                    success is List<*> -> {
                        val astList = success.filterIsInstance<Ast>()
                        if (success.size != astList.size) {
                            "unsupported list content".astError()
                        } else {
                            val replace = astList.map { ast ->
                                if (attachments != null && ast is AstWithAttachments) {
                                    // copy all attachments from a
                                    ast.withAttachments(attachments + ast.attachments)
                                } else {
                                    ast
                                }
                            }.map { ast ->
                                if (ast is AstWithRawAst && ast.raw == null) {
                                    ast.withRaw(RawAst(a))
                                } else {
                                    ast
                                }
                            }.wrapWithIndex(nextAstId)
                            astSuccess(
                                copy(
                                    mapper = mapper.reset(),
                                    ast = ast.replace(replace),
                                    skip = replace.fold(skip) { skip, astWrapper ->
                                        skip.add(astWrapper, m)
                                    },
                                    nextAstId = nextAstId + replace.size
                                )
                            )
                        }
                    }
                    else ->
                        "unsupported result type".astError()
                }
            }
            is AstFailure<State, *> ->
                result.errors.astError()
            else ->
                // TODO sealed?
                "unpexected return type".astError()
        }
    } else {
        astSuccess(copy(mapper = mapper.next()))
    }
}

private data class DefaultAstSuccess<State, T>(
    override val state: State,
    override val factory: TreeMapResultFactory<State>,
    override val success: T
) : AstSuccess<State, T> {
    override fun <T2> map(
        mapper: TreeMapResultFactory<State>.(T) -> T2
    ): AstResult<State, T2> {
        return DefaultAstSuccess(state, factory, factory.mapper(success))
    }

    override fun <Other, T2> flatMap(
        mapper: TreeMapResultFactory<State>.(T) -> AstResult<Other, T2>
    ): AstResult<Other, T2> {
        return factory.mapper(success)
    }
}

private data class DefaultAstFailure<State, T>(
    override val state: State, // TODO remove
    override val factory: TreeMapResultFactory<State>,
    override val errors: List<String>
) : AstFailure<State, T> {

    @Suppress("UNCHECKED_CAST")
    override fun <T2> map(
        mapper: TreeMapResultFactory<State>.(T) -> T2
    ): AstResult<State, T2> {
        return this as AstResult<State, T2>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Other, T2> flatMap(
        mapper: TreeMapResultFactory<State>.(T) -> AstResult<Other, T2>
    ): AstResult<Other, T2> {
        return this as AstResult<Other, T2>
    }
}
