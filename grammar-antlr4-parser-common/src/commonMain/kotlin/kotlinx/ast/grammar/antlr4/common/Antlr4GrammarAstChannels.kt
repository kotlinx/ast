package kotlinx.ast.grammar.antlr4.common

import kotlinx.ast.common.AstChannel

object Antlr4GrammarAstChannels {
    val default = AstChannel(0, "DEFAULT_TOKEN_CHANNEL")
    val off = AstChannel(1, "OFF_CHANNEL")
    val comment = AstChannel(2, "COMMENT")
}
