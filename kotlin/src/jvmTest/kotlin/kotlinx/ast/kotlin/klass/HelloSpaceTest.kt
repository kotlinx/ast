package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.kotlin.KotlinAstParserType

class HelloSpaceTest : KlassTest(
    "Hello<Space>",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleIdentifier,
            KotlinAstParserType.token,
            KotlinAstParserType.simpleIdentifier,
            KotlinAstParserType.token
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
            KlassIdentifier("Hello"),
            terminal("LANGLE", "<"),
            KlassIdentifier("Space"),
            terminal("RANGLE", ">")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.identifier,
            KotlinAstParserType.token,
            KotlinAstParserType.identifier,
            KotlinAstParserType.token
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
            KlassIdentifier("Hello"),
            terminal("LANGLE", "<"),
            KlassIdentifier("Space"),
            terminal("RANGLE", ">")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleUserType
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
            KlassIdentifier("Hello").parameterizedBy(
                KlassIdentifier("Space")
            )
        )
    ),
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
                                  Identifier >>>Space<<< (DEFAULT_TOKEN_CHANNEL)
                      RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello").parameterizedBy(
                KlassIdentifier("Space")
            )
        )
    )
)
