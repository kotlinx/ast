package kotlinx.ast.common.map

import kotlinx.ast.common.*
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.DefaultAstNode

private data class TreeMapStatus(
    val changed: Boolean = false,
    val list: List<Ast> = emptyList(),
    val skip: Map<TreeMapAstMapper, Set<Ast>> = emptyMap()
)

interface TreeMap {
    fun treeMap(ast: Ast, attachRawAst: Boolean): AstResult<List<Ast>> = treeMap(listOf(ast), attachRawAst)

    fun treeMap(list: List<Ast>, attachRawAst: Boolean): AstResult<List<Ast>>
}

class DefaultTreeMap(
    private val mapper: TreeMapMapper
) : TreeMap {
    constructor(
        vararg mapper: TreeMapMapper
    ) : this(
        mapper = TreeMapMapper(mapper.flatMap(TreeMapMapper::mapper))
    )

    override fun treeMap(list: List<Ast>, attachRawAst: Boolean): AstResult<List<Ast>> {
        return treeMapList(mapper, list, emptyMap(), attachRawAst = attachRawAst)
    }
}

class TreeMapContext(
    treeMap: TreeMap,
    val context: Ast,
    override val attachRawAst: Boolean
) : TreeMap by treeMap, TreeMapWithRawAst

private fun TreeMap.treeMapMapper(
    list: List<Ast>,
    skip: Map<TreeMapAstMapper, Set<Ast>>,
    mapper: TreeMapAstMapper,
    attachRawAst: Boolean
): AstResult<TreeMapStatus> {
    return list.fold(
        astSuccess(TreeMapStatus(skip = skip))
    ) { astResult, ast ->
        astResult.flatMap { status ->
            if (skip[mapper]?.contains(ast) == true) {
                astSuccess(
                    status.copy(
                        list = status.list + ast
                    )
                )
            } else {
                mapper.map(TreeMapContext(this, ast, attachRawAst)).flatMap { result ->
                    when (result) {
                        is TreeMapResult.Failure ->
                            astFailure(result)

                        is TreeMapResult.Keep ->
                            astSuccess(
                                status.copy(
                                    list = status.list + ast
                                )
                            )

                        is TreeMapResult.Drop ->
                            astSuccess(
                                status.copy(
                                    changed = true
                                )
                            )

                        is TreeMapResult.Continue ->
                            astSuccess(
                                status.copy(
                                    changed = true,
                                    list = status.list + result.list
                                )
                            )

                        is TreeMapResult.Skip -> {
                            val skipList = status.skip.getOrElse(mapper, { emptySet() }) + result.list
                            astSuccess(
                                status.copy(
                                    changed = true,
                                    list = status.list + result.list,
                                    skip = status.skip + (mapper to skipList)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private tailrec fun TreeMap.treeMapList(
    treeMapMapper: TreeMapMapper,
    list: List<Ast>,
    skip: Map<TreeMapAstMapper, Set<Ast>>,
    attachRawAst: Boolean
): AstResult<List<Ast>> {
    return if (list.isEmpty()) {
        astSuccess(emptyList())
    } else {
        val result = treeMapMapper.mapper.fold(
            astSuccess(TreeMapStatus(skip = skip))
        ) { result, mapper ->
            result.flatMap { status ->
                if (status.changed) {
                    astSuccess(status)
                } else {
                    treeMapMapper(list, status.skip, mapper, attachRawAst = attachRawAst)
                }
            }
        }

        when (result) {
            is AstSuccess -> {
                val status = result.result
                if (status.changed) {
                    treeMapList(treeMapMapper, status.list, status.skip, attachRawAst = attachRawAst)
                } else {
                    list.map { ast ->
                        when (ast) {
                            is DefaultAstNode ->
                                @Suppress("NON_TAIL_RECURSIVE_CALL")
                                treeMapList(treeMapMapper, ast.children, status.skip, attachRawAst = attachRawAst)
                                    .map<Ast> { children ->
                                        ast.copy(children = children)
                                    }
                            else ->
                                astSuccess(ast)
                        }
                    }.flatten()
                }
            }
            is AstFailure ->
                astFailure(result)
            else ->
                astFailure("should not happen")
        }
    }
}
