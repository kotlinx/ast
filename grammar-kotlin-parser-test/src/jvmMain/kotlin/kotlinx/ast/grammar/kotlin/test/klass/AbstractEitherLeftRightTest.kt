package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractEitherLeftRightTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "Either<Left, Right>",
        TestCase(
            parserTypes = listOf(
                KotlinGrammarParserType.simpleUserType
            ),
            print = """
                simpleUserType
                  simpleIdentifier
                    Identifier >>>Either<<< (DEFAULT_TOKEN_CHANNEL)
                  typeArguments
                    LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                    typeProjection
                      type
                        typeReference
                          userType
                            simpleUserType
                              simpleIdentifier
                                Identifier >>>Left<<< (DEFAULT_TOKEN_CHANNEL)
                    COMMA >>>,<<< (DEFAULT_TOKEN_CHANNEL)
                    WS >>> <<< (HIDDEN)
                    typeProjection
                      type
                        typeReference
                          userType
                            simpleUserType
                              simpleIdentifier
                                Identifier >>>Right<<< (DEFAULT_TOKEN_CHANNEL)
                    RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Either", raw = null).parameterizedBy(
                    KlassIdentifier("Left", raw = null),
                    KlassIdentifier("Right", raw = null)
                )
            )
        )
    )
