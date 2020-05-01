package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.klass.starProjection
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractStarProjectionTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "Hello<*>",
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
                      MULT >>>*<<< (DEFAULT_TOKEN_CHANNEL)
                    RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassIdentifier("Hello", raw = null).parameterizedBy(
                    starProjection
                )
            )
        )
    )
