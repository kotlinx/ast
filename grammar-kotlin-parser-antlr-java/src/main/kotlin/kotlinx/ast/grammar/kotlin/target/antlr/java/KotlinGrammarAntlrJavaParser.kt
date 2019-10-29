package kotlinx.ast.grammar.kotlin.target.antlr.java

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.target.antlr.java.generated.KotlinLexer
import kotlinx.ast.grammar.kotlin.target.antlr.java.generated.KotlinParser
import kotlinx.ast.parser.antlr.java.AntlrJavaParser
import org.antlr.v4.runtime.tree.ParseTree

object KotlinGrammarAntlrJavaParser : KotlinGrammarParser<KotlinParser, ParseTree>,
    AntlrJavaParser<KotlinParser, KotlinGrammarParserType>(
        KotlinGrammarAntlrJavaParserExtractor,
        ::KotlinLexer,
        ::KotlinParser
    )
