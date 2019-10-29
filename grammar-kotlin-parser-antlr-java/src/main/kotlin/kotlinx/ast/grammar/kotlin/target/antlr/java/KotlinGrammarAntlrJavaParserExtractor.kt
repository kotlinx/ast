package kotlinx.ast.grammar.kotlin.target.antlr.java

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserExtractor
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.target.antlr.java.generated.KotlinParser
import kotlinx.ast.parser.antlr.java.AntlrJavaParserExtractor
import org.antlr.v4.runtime.tree.ParseTree

object KotlinGrammarAntlrJavaParserExtractor
    : AntlrJavaParserExtractor<KotlinParser, KotlinGrammarParserType>,
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
