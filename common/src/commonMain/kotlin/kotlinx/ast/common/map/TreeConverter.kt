package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast

interface TreeConverter<State> {
    fun convert(context: TreeMapContext<State>, ast: Ast): AstResult<State, List<Ast>>?

    companion object {
        operator fun <State> invoke(
            convert: (TreeMapContext<State>, Ast) -> AstResult<State, List<Ast>>?
        ): TreeConverter<State> {
            return DefaultTreeConverter(convert)
        }
    }
}

data class DefaultTreeConverter<State>(
    val convert: (TreeMapContext<State>, Ast) -> AstResult<State, List<Ast>>?
) : TreeConverter<State> {
    override fun convert(context: TreeMapContext<State>, ast: Ast): AstResult<State, List<Ast>>? {
        return convert.invoke(context, ast)
    }
}
