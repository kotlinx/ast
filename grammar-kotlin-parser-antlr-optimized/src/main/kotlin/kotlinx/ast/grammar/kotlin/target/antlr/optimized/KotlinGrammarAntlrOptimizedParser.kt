package kotlinx.ast.grammar.kotlin.target.antlr.optimized

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.target.antlr.optimized.generated.KotlinLexer
import kotlinx.ast.grammar.kotlin.target.antlr.optimized.generated.KotlinParser
import kotlinx.ast.parser.antlr.java.AntlrJavaParser
import org.antlr.v4.runtime.tree.ParseTree

object KotlinGrammarAntlrOptimizedParser : KotlinGrammarParser<KotlinParser, ParseTree>,
    AntlrJavaParser<KotlinParser, KotlinGrammarParserType>(
        KotlinGrammarAntlrOptimizedParserExtractor,
        ::KotlinLexer,
        ::KotlinParser
    )
