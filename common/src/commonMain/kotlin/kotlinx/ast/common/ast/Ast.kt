package kotlinx.ast.common.ast

import kotlinx.ast.common.AstChannel

interface Ast {
    val description: String
}

interface AstTerminal : Ast {
    val text: String

    companion object {
        operator fun <T> invoke(
            description: String,
            text: String,
            channel: AstChannel
        ): AstTerminal {
            return DefaultAstTerminal(description, text, channel)
        }
    }
}

data class DefaultAstTerminal(
    override val description: String,
    override val text: String,
    val channel: AstChannel
) : AstTerminal

interface AstNode : Ast {
    val children: List<Ast>

    companion object {
        operator fun <T> invoke(
            description: String,
            children: List<Ast>
        ): AstNode {
            return DefaultAstNode(description, children)
        }
    }
}

data class DefaultAstNode(
    override val description: String,
    override val children: List<Ast>
) : AstNode

interface AstGroup : Ast
