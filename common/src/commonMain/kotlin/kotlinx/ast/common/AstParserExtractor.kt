package kotlinx.ast.common

interface AstParserExtractor<Parser, Tree, Type : AstParserType> {
    fun extract(parser: Parser, type: Type): Tree {
        return extractor(type)(parser)
    }

    fun extractor(type: Type): (Parser) -> Tree
}
