package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstChannel

object KotlinGrammarAstChannels {
    val default = AstChannel(0, "DEFAULT_TOKEN_CHANNEL")
    val hidden = AstChannel(1, "HIDDEN")
    val comment = AstChannel(2, "COMMENT")
}
