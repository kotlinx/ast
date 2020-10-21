package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode

data class TreeFilterByChildren(
    val treeFilter: List<TreeFilter>
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return when (ast) {
            is AstNode ->
                ast.children.any { child ->
                    treeFilter.any { filter ->
                        filter.matches(child)
                    }
                }
            else -> false
        }
    }
}

fun byChildren(vararg treeFilter: TreeFilter): TreeFilter {
    return TreeFilterByChildren(treeFilter.toList())
}
