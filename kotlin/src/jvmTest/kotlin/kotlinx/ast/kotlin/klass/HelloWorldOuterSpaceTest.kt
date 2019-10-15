package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.kotlin.KotlinAstParserType

class HelloWorldOuterSpaceTest : KlassTest(
    "Hello<World>.Outer<Space>",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.userType
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
            KlassIdentifier("Hello").parameterizedBy(
                KlassIdentifier("World")
            ),
            KlassIdentifier("Outer").parameterizedBy(
                KlassIdentifier("Space")
            )
        )
    )
)
