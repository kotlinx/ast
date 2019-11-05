package kotlinx.ast.grammar.antlr4.common

import kotlinx.ast.common.AstChannel

object Antlr4GrammarAstChannels {
    val default = AstChannel(0, "DEFAULT_TOKEN_CHANNEL")
    val hidden = AstChannel(1, "HIDDEN")
    val off = AstChannel(2, "OFF_CHANNEL")
    val comment = AstChannel(3, "COMMENT")
}
