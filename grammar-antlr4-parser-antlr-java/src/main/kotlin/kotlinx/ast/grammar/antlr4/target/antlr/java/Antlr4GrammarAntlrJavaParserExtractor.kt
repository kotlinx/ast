package kotlinx.ast.grammar.antlr4.target.antlr.java

import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParserExtractor
import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParserType
import kotlinx.ast.parser.antlr.java.AntlrJavaParserExtractor
import org.antlr.parser.antlr4.ANTLRv4Parser
import org.antlr.v4.runtime.tree.ParseTree

object Antlr4GrammarAntlrJavaParserExtractor
    : AntlrJavaParserExtractor<ANTLRv4Parser, Antlr4GrammarParserType>,
    Antlr4GrammarParserExtractor<ANTLRv4Parser, ParseTree> {

    override fun extractor(type: Antlr4GrammarParserType): (ANTLRv4Parser) -> ParseTree {
        return when (type) {
            Antlr4GrammarParserType.token ->
                throw RuntimeException("token should be handled by internal ast parser")
            Antlr4GrammarParserType.grammarSpec ->
                ANTLRv4Parser::grammarSpec
        }
    }
}
