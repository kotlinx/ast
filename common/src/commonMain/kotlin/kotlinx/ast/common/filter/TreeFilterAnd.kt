package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

data class TreeFilterAnd(
    val filters: List<TreeFilter>
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return filters.all {
            it.matches(ast)
        }
    }
}

internal fun and(first: TreeFilter, other: TreeFilter): TreeFilter {
    val left = if (first is TreeFilterAnd) {
        first.filters
    } else {
        listOf(first)
    }
    val right = if (other is TreeFilterAnd) {
        other.filters
    } else {
        listOf(other)
    }
    return TreeFilterAnd(left + right)
}
