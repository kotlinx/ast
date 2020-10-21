package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.filter.TreeFilter

data class TreeMapBuilder<State>(
    val mapper: List<TreeMapper<State>> = emptyList()
) {
    inline fun <reified A : Ast> convert(
        filter: TreeFilter,
        noinline convert: TreeMapContext<State>.(A) -> AstResult<State, List<Ast>>
    ): TreeMapBuilder<State> {
        return copy(
            mapper = mapper + TreeMapper(filter) { context, ast ->
                if (ast is A) {
                    convert(context, ast)
                } else {
                    null
                }
            }
        )
    }

    operator fun invoke(
        state: State,
        ast: List<Ast>,
        attachRawAst: Boolean
    ): AstResult<State, List<Ast>> {
        return treeMap(state, mapper, ast, attachRawAst)
    }

    companion object {
        operator fun <State> invoke(vararg builders: TreeMapBuilder<State>): TreeMapBuilder<State> {
            return TreeMapBuilder(builders.flatMap(TreeMapBuilder<State>::mapper))
        }
    }
}
