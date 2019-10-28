package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.DefaultAstNode
import kotlinx.ast.common.ast.DefaultAstTerminal
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarAstChannels

internal fun terminal(
    name: String,
    text: String,
    channel: AstChannel = KotlinGrammarAstChannels.default
): DefaultAstTerminal {
    return DefaultAstTerminal(name, text, channel)
}

internal fun node(name: String, children: List<Ast>): AstNode {
    return DefaultAstNode(name, children)
}

internal fun node(name: String, child: Ast): AstNode {
    return DefaultAstNode(name, listOf(child))
}
