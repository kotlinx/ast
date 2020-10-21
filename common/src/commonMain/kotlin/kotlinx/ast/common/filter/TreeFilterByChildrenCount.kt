package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode

data class TreeFilterByChildrenCount(
    val min: Int?,
    val max: Int?
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return when (ast) {
            is AstNode -> {
                val size = ast.children.size
                if (min == null) {
                    true
                } else {
                    size >= min
                } && if (max == null) {
                    true
                } else {
                    size <= max
                }
            }
            else -> false
        }
    }
}

fun byChildrenCount(min: Int? = null, max: Int? = null): TreeFilter {
    return TreeFilterByChildrenCount(min, max)
}

val byChildrenCountMax1: TreeFilter = byChildrenCount(max = 1)
val byChildrenCountMin2: TreeFilter = byChildrenCount(min = 2)
