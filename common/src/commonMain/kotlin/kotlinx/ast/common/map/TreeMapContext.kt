package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.filter.TreeFilter

abstract class TreeMapContext<State> : TreeMapResultFactory<State> {
    abstract fun recursive(nodes: List<Ast>): AstResult<State, List<Ast>>

    fun recursiveChildren(
        node: AstNode,
        filter: TreeFilter? = null
    ): AstResult<State, List<Ast>> {
        val children = if (filter == null) {
            node.children
        } else {
            filter(node.children)
        }
        return recursive(children).flatMap { mapped ->
            with(node) {
                withChildren(mapped)
            }
        }.toAstList()
    }

    fun recursiveFlatten(
        node: AstNode,
        filter: TreeFilter? = null
    ): AstResult<State, List<Ast>> {
        return recursiveFlattenInternal(node, filter, flattenSingle = false)
    }

    fun recursiveFlattenSingle(
        node: AstNode,
        filter: TreeFilter? = null
    ): AstResult<State, List<Ast>> {
        return recursiveFlattenInternal(node, filter, flattenSingle = true)
    }

    fun AstResult<State, Ast>.toAstList(): AstResult<State, List<Ast>> {
        return flatMap { ast ->
            astContinue(ast)
        }
    }

    abstract fun List<Ast>.astFold(
        mapper: TreeMapContext<State>.(Ast) -> AstResult<State, List<Ast>>
    ): AstResult<State, List<Ast>>

    fun flatten(
        node: AstNode,
        filter: TreeFilter? = null
    ): AstResult<State, List<Ast>> {
        val children = if (filter == null) {
            node.children
        } else {
            filter(node.children)
        }
        return astSuccess(children)
    }

    inline fun <reified T : AstNode> T.filterChildren(filter: TreeFilter): AstResult<State, T> {
        return this@TreeMapContext.filterChildren(filter).flatMap { ast ->
            val t = ast as? T
            if (t == null) {
                "filterChildren failed".astError()
            } else {
                astSuccess(t)
            }
        }
    }
}

internal fun <State> TreeMapContext<State>.recursiveFlattenInternal(
    node: AstNode,
    filter: TreeFilter?,
    flattenSingle: Boolean,
): AstResult<State, List<Ast>> {
    val children = if (filter == null) {
        node.children
    } else {
        filter(node.children)
    }
    return if (flattenSingle && children.size == 1) {
        val first = children.first()
        if (first is AstNode) {
            recursive(listOf(first))
        } else {
            astContinue(first)
        }
    } else {
        val patched = if (filter == null) {
            astSuccess(node)
        } else {
            with(node) {
                withChildren(children)
            }
        }
        patched.flatMap { ast ->
            if (flattenSingle) {
                recursiveChildren(ast)
            } else {
                recursive(ast.children)
            }
        }
    }
}
