package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstParser
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast

interface KotlinGrammarParser<Parser, Tree> : AstParser<Parser, Tree, KotlinGrammarParserType> {
    fun parseKotlinFile(source: AstSource): Ast {
        return parse(source, KotlinGrammarParserType.kotlinFile)
    }
}
