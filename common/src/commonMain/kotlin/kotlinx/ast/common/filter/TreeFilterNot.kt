package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

data class TreeFilterNot(
    val filter: TreeFilter
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return !filter.matches(ast)
    }
}

internal fun not(first: TreeFilter): TreeFilter {
    return if (first is TreeFilterNot) {
        first.filter
    } else {
        return TreeFilterNot(first)
    }
}
