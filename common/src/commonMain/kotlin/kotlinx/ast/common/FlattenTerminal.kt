package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal

fun List<Ast>.flattenTerminal(): List<AstTerminal> {
    return flatMap(Ast::flattenTerminal)
}

fun Ast.flattenTerminal(): List<AstTerminal> {
    return when (this) {
        is AstNode ->
            children.flatMap(Ast::flattenTerminal)
        is AstTerminal ->
            listOf(this)
        else ->
            emptyList()
    }
}
