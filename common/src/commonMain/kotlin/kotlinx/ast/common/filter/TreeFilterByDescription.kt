package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

data class TreeFilterByDescription(
    val descriptions: Set<String>
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return descriptions.contains(ast.description)
    }
}

fun byDescription(description: String, vararg other: String): TreeFilter {
    return TreeFilterByDescription(setOf(description) + other)
}
