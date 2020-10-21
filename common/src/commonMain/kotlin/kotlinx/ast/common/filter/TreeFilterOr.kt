package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

data class TreeFilterOr(
    val filters: List<TreeFilter>
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return filters.any {
            it.matches(ast)
        }
    }
}

internal fun or(first: TreeFilter, other: TreeFilter): TreeFilter {
    val left = if (first is TreeFilterOr) {
        first.filters
    } else {
        listOf(first)
    }
    val right = if (other is TreeFilterOr) {
        other.filters
    } else {
        listOf(other)
    }
    return TreeFilterOr(left + right)
}
