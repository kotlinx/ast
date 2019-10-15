package kotlinx.ast.common.ast

import kotlinx.ast.common.AstChannel

interface Ast {
    val description: String
}

interface AstTerminal : Ast {
    val text: String
}

data class DefaultAstTerminal(
    override val description: String,
    override val text: String,
    val channel: AstChannel
) : AstTerminal

interface AstNode : Ast {
    val children: List<Ast>
}

data class DefaultAstNode(
    override val description: String,
    override val children: List<Ast>
) : AstNode

interface AstGroup : Ast
