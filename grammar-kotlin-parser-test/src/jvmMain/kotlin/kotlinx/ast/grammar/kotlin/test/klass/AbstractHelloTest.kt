package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractHelloTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) : AbstractKlassTest<Parser>(
    parser,
    "Hello",
    TestCase(
        parserTypes = listOf(
            KotlinGrammarParserType.simpleIdentifier
        ),
        print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier(ignoredByUnitTest, "Hello")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinGrammarParserType.identifier
        ),
        print = """
                identifier
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier(ignoredByUnitTest, "Hello")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinGrammarParserType.simpleUserType
        ),
        print = """
                simpleUserType
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier(ignoredByUnitTest, "Hello")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinGrammarParserType.userType
        ),
        print = """
                userType
                  simpleUserType
                    simpleIdentifier
                      Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier(ignoredByUnitTest, "Hello")
        )
    )
)
