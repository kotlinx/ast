package kotlinx.ast.grammar.antlr4.common

import kotlinx.ast.common.AstParserExtractor

interface Antlr4GrammarParserExtractor<Parser, Tree> : AstParserExtractor<Parser, Tree, Antlr4GrammarParserType>
