package kotlinx.ast.grammar.kotlin.target.antlr.kotlin

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.generated.KotlinLexer
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.generated.KotlinParser
import kotlinx.ast.parser.antlr.kotlin.AntlrKotlinParser
import org.antlr.v4.kotlinruntime.tree.ParseTree

object KotlinGrammarAntlrKotlinParser : KotlinGrammarParser<KotlinParser, ParseTree>,
    AntlrKotlinParser<KotlinParser, KotlinGrammarParserType>(
        KotlinGrammarAntlrKotlinParserExtractor,
        ::KotlinLexer,
        ::KotlinParser
    )
