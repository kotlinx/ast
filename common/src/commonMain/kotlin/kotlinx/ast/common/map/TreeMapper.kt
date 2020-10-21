package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.filter.TreeFilter

interface TreeMapper<State> {
    val filter: TreeFilter
    val converter: TreeConverter<State>

    companion object {
        operator fun <State> invoke(
            filter: TreeFilter,
            converter: (TreeMapContext<State>, Ast) -> AstResult<State, List<Ast>>?
        ): TreeMapper<State> {
            return DefaultTreeMapper(filter, TreeConverter { context, ast ->
                converter(context, ast)
            })
        }
    }
}

data class DefaultTreeMapper<State>(
    override val filter: TreeFilter,
    override val converter: TreeConverter<State>
) : TreeMapper<State>
