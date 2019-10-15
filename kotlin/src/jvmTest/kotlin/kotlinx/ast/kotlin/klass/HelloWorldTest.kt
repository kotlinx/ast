package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.kotlin.KotlinAstParserType

class HelloWorldTest : KlassTest(
    "Hello.World",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleIdentifier,
            KotlinAstParserType.token,
            KotlinAstParserType.simpleIdentifier
        ),
        print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                simpleIdentifier
                  Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello"),
            terminal("DOT", "."),
            KlassIdentifier("World")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.identifier
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
            KlassIdentifier("Hello"),
            KlassIdentifier("World")
        )
    ),
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleUserType,
            KotlinAstParserType.token,
            KotlinAstParserType.simpleUserType
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
            KlassIdentifier("Hello"),
            terminal("DOT", "."),
            KlassIdentifier("World")
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
                  DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                  simpleUserType
                    simpleIdentifier
                      Identifier >>>World<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello"),
            KlassIdentifier("World")
        )
    )
)
