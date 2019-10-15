package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.kotlin.KotlinAstParserType

class EitherLeftRightTest : KlassTest(
    "Either<Left, Right>",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleUserType
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
            KlassIdentifier("Either").parameterizedBy(
                KlassIdentifier("Left"),
                KlassIdentifier("Right")
            )
        )
    )
)
