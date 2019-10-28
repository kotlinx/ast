package kotlinx.ast.grammar.kotlin.target.antlr.kotlin

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserExtractor
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.generated.KotlinParser
import kotlinx.ast.parser.antlr.kotlin.AntlrKotlinParserExtractor
import org.antlr.v4.kotlinruntime.tree.ParseTree

object KotlinGrammarAntlrKotlinParserExtractor
    : AntlrKotlinParserExtractor<KotlinParser, KotlinGrammarParserType>,
    KotlinGrammarParserExtractor<KotlinParser, ParseTree> {

    override fun extractor(type: KotlinGrammarParserType): (KotlinParser) -> ParseTree {
        return when (type) {
            KotlinGrammarParserType.token ->
                throw RuntimeException("token should be handled by internal ast parser")
            KotlinGrammarParserType.kotlinFile ->
                KotlinParser::kotlinFile
            KotlinGrammarParserType.identifier ->
                KotlinParser::identifier
            KotlinGrammarParserType.importList ->
                KotlinParser::importList
            KotlinGrammarParserType.simpleIdentifier ->
                KotlinParser::simpleIdentifier
            KotlinGrammarParserType.`annotation` ->
                KotlinParser::annotation
            KotlinGrammarParserType.typeArguments ->
                KotlinParser::typeArguments
            KotlinGrammarParserType.simpleUserType ->
                KotlinParser::simpleUserType
            KotlinGrammarParserType.userType ->
                KotlinParser::userType
        }
    }
}
