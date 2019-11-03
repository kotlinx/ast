package kotlinx.ast.grammar.antlr4.common

import kotlinx.ast.common.AstParser
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast

interface Antlr4GrammarParser<Parser, Tree> : AstParser<Parser, Tree, Antlr4GrammarParserType> {
    fun parseGrammarSpec(source: AstSource): Ast {
        return parse(source, Antlr4GrammarParserType.grammarSpec)
    }
}
