package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractHelloWorldOuterSpaceTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "Hello<World>.Outer<Space>",
        TestCase(
            name = "AbstractHelloWorldOuterSpaceTest",
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
                                  Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                      RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                  DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                  simpleUserType
                    simpleIdentifier
                      Identifier >>>Outer<<< (DEFAULT_TOKEN_CHANNEL)
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
                KlassIdentifier("Hello", raw = null).parameterizedBy(
                    KlassIdentifier("World", raw = null)
                ),
                KlassIdentifier("Outer", raw = null).parameterizedBy(
                    KlassIdentifier("Space", raw = null)
                )
            )
        )
    )
