package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractHelloSpaceTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "Hello<Space>",
        TestCase(
            parserTypes = listOf(
                KotlinGrammarParserType.simpleIdentifier,
                KotlinGrammarParserType.token,
                KotlinGrammarParserType.simpleIdentifier,
                KotlinGrammarParserType.token
            ),
            print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                simpleIdentifier
                  Identifier >>>Space<<< (DEFAULT_TOKEN_CHANNEL)
                RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier(ignoredByUnitTest, "Hello"),
                terminal("LANGLE", "<"),
                KlassIdentifier(ignoredByUnitTest, "Space"),
                terminal("RANGLE", ">")
            )
        ),
        TestCase(
            parserTypes = listOf(
                KotlinGrammarParserType.identifier,
                KotlinGrammarParserType.token,
                KotlinGrammarParserType.identifier,
                KotlinGrammarParserType.token
            ),
            print = """
                identifier
                  simpleIdentifier
                    Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                identifier
                  simpleIdentifier
                    Identifier >>>Space<<< (DEFAULT_TOKEN_CHANNEL)
                RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier(ignoredByUnitTest, "Hello"),
                terminal("LANGLE", "<"),
                KlassIdentifier(ignoredByUnitTest, "Space"),
                terminal("RANGLE", ">")
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
                  typeArguments
                    LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                    typeProjection
                      type
                        typeReference
                          userType
                            simpleUserType
                              simpleIdentifier
                                Identifier >>>Space<<< (DEFAULT_TOKEN_CHANNEL)
                    RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier(ignoredByUnitTest, "Hello").parameterizedBy(
                    KlassIdentifier(ignoredByUnitTest, "Space")
                )
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
                    typeArguments
                      LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                      typeProjection
                        type
                          typeReference
                            userType
                              simpleUserType
                                simpleIdentifier
                                  Identifier >>>Space<<< (DEFAULT_TOKEN_CHANNEL)
                      RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier(ignoredByUnitTest, "Hello").parameterizedBy(
                    KlassIdentifier(ignoredByUnitTest, "Space")
                )
            )
        )
    )
