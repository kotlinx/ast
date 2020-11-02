package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractHelloWorldTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "Hello.World",
        TestCase(
            name = "AbstractHelloWorldTest1",
            parserTypes = listOf(
                KotlinGrammarParserType.simpleIdentifier,
                KotlinGrammarParserType.token,
                KotlinGrammarParserType.simpleIdentifier
            ),
            print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                simpleIdentifier
                  Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Hello", raw = null),
                terminal("DOT", "."),
                KlassIdentifier("World", raw = null)
            )
        ),
        TestCase(
            name = "AbstractHelloWorldTest2",
            parserTypes = listOf(
                KotlinGrammarParserType.identifier
            ),
            print = """
                identifier
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                  DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                  simpleIdentifier
                    Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Hello", raw = null),
                KlassIdentifier("World", raw = null)
            )
        ),
        TestCase(
            name = "AbstractHelloWorldTest3",
            parserTypes = listOf(
                KotlinGrammarParserType.simpleUserType,
                KotlinGrammarParserType.token,
                KotlinGrammarParserType.simpleUserType
            ),
            print = """
                simpleUserType
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                simpleUserType
                  simpleIdentifier
                    Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Hello", raw = null),
                terminal("DOT", "."),
                KlassIdentifier("World", raw = null)
            )
        ),
        TestCase(
            name = "AbstractHelloWorldTest4",
            parserTypes = listOf(
                KotlinGrammarParserType.userType
            ),
            print = """
                userType
                  simpleUserType
                    simpleIdentifier
                      Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                  DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                  simpleUserType
                    simpleIdentifier
                      Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Hello", raw = null),
                KlassIdentifier("World", raw = null)
            )
        )
    )
