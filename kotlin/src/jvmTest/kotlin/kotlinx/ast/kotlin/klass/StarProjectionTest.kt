package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.klass.starProjection
import kotlinx.ast.kotlin.KotlinAstParserType

class StarProjectionTest : KlassTest(
    "Hello<*>",
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
                      MULT >>>*<<< (DEFAULT_TOKEN_CHANNEL)
                    RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
        summary = listOf(
            KlassIdentifier("Hello").parameterizedBy(
                starProjection
            )
        )
    )
)
