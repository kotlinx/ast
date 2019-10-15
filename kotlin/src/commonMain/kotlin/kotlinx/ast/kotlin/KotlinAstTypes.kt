package kotlinx.ast.kotlin

import kotlinx.ast.common.AstParserType
import kotlinx.ast.kotlin.generated.KotlinParser
import org.antlr.v4.kotlinruntime.tree.ParseTree

enum class KotlinAstParserType(
    private val extractor: (KotlinParser) -> ParseTree
) : AstParserType<KotlinParser> {
    token({ _ -> throw RuntimeException("token should be handled by internal ast parser") }),
    kotlinFile(KotlinParser::kotlinFile),
    identifier(KotlinParser::identifier),
    importList(KotlinParser::importList),
    simpleIdentifier(KotlinParser::simpleIdentifier),
    `annotation`(KotlinParser::annotation),
    typeArguments(KotlinParser::typeArguments),
    simpleUserType(KotlinParser::simpleUserType),
    userType(KotlinParser::userType)
    ;

    override fun extract(parser: KotlinParser): ParseTree {
        return extractor(parser)
    }
}
