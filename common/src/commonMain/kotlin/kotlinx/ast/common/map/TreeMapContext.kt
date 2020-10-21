package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.filter.TreeFilter
import kotlinx.ast.common.klass.RawAst

abstract class TreeMapContext<State> : TreeMapResultFactory<State> {
    abstract fun recursive(nodes: List<Ast>): AstResult<State, List<Ast>>

    abstract fun recursive(node: AstNode): AstResult<State, AstNode>

    fun AstResult<State, Ast>.toAstList(): AstResult<State, List<Ast>> {
        return flatMap { ast ->
            astContinueList(ast)
        }
    }

    abstract fun attachRaw(raw: Ast): RawAst?

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
        return astContinue(children)
    }

    inline fun <reified T : AstNode> T.filterChildren(filter: TreeFilter): AstResult<State, T> {
        return this@TreeMapContext.filterChildren(filter).flatMap { ast ->
            val t = ast as? T
            if (t == null) {
                "".astError()
            } else {
                astContinue(t)
            }
        }
    }
}
