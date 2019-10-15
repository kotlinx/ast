package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.kotlin.KotlinAstParserType

class HelloTest : KlassTest(
    "Hello",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.simpleIdentifier
        ),
        print = """
                simpleIdentifier
                  Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello")
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
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello")
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
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello")
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
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello")
        )
    )
)
