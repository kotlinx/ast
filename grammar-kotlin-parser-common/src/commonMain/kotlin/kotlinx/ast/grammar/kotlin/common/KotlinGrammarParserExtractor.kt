package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstParserExtractor

interface KotlinGrammarParserExtractor<Parser, Tree> : AstParserExtractor<Parser, Tree, KotlinGrammarParserType>
