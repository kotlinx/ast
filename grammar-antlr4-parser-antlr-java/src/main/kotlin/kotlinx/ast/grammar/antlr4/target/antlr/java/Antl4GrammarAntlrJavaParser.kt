package kotlinx.ast.grammar.antlr4.target.antlr.java

import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParser
import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParserType
import kotlinx.ast.parser.antlr.java.AntlrJavaParser
import org.antlr.parser.antlr4.ANTLRv4Lexer
import org.antlr.parser.antlr4.ANTLRv4Parser
import org.antlr.v4.runtime.tree.ParseTree

object Antl4GrammarAntlrJavaParser : Antlr4GrammarParser<ANTLRv4Parser, ParseTree>,
    AntlrJavaParser<ANTLRv4Parser, Antlr4GrammarParserType>(
        Antlr4GrammarAntlrJavaParserExtractor,
        ::ANTLRv4Lexer,
        ::ANTLRv4Parser
    )
