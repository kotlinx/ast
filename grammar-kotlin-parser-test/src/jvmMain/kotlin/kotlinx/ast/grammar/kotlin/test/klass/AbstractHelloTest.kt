package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractHelloTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) : AbstractKlassTest<Parser>(
    parser,
    "Hello",
    TestCase(
        name = "AbstractHelloTest.simpleIdentifier",
        parserTypes = listOf(
            KotlinGrammarParserType.simpleIdentifier
        ),
        print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello", raw = null)
        )
    ),
    TestCase(
        name = "AbstractHelloTest.identifier",
        parserTypes = listOf(
            KotlinGrammarParserType.identifier
        ),
        print = """
                identifier
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello", raw = null)
        )
    ),
    TestCase(
        name = "AbstractHelloTest.simpleUserType",
        parserTypes = listOf(
            KotlinGrammarParserType.simpleUserType
        ),
        print = """
                simpleUserType
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello", raw = null)
        )
    ),
    TestCase(
        name = "AbstractHelloTest.userType",
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
            KlassIdentifier("Hello", raw = null)
        )
    )
)
