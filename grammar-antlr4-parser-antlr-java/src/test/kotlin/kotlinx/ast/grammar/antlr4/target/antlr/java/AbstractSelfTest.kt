package kotlinx.ast.grammar.antlr4.target.antlr.java

import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParser
import kotlinx.ast.test.pathMap
import kotlinx.ast.test.pathOf

abstract class AbstractSelfTest<Parser : Antlr4GrammarParser<*, *>>(
    parser: Parser
) : kotlinx.ast.test.AbstractDirectoryTest({ _, source ->
    parser.parseGrammarSpec(source)
}, pathOf("..").pathMap(".g4"))
