package kotlinx.ast.grammar.kotlin.test.klass

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.klass.detachRaw
import kotlinx.ast.common.map.DefaultTreeMapResultFactory
import kotlinx.ast.common.printString
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.summary

abstract class AbstractKlassTest<Parser : KotlinGrammarParser<*, *>>(
    parser: Parser,
    code: String,
    vararg testCases: TestCase
) : FunSpec({
    testCases.forEach { testCase ->
        context(testCase.toString()) {
            val given by lazy {
                parser.parse(AstSource.String(code), testCase.parserTypes)
            }

            fun <T> check(actual: T, expected: T) {
                if (actual != expected) {
                    println("Expected :$expected")
                    println("Actual   :$actual")
                }
                actual.shouldBe(expected)
            }

            fun <State, T> checkAstResult(actual: AstResult<State, T>, expected: T) {
                if (actual !is AstSuccess) {
                    fail("actual is no AstSuccess")
                }
                check(actual.success, expected)
            }

            test("print") {
                val actual = given.joinToString(
                    separator = "",
                    transform = Ast::printString
                )
                check(actual, testCase.print + "\n")
            }

            test("summary") {
                with(DefaultTreeMapResultFactory(Unit)) {
                    val actual = flatten(given.map { ast ->
                        ast.summary(attachRawAst = false).flatMap { list ->
                            Unit.astSuccess(list)
                        }
                    }).get().flatten().detachRaw()
                    checkAstResult(actual, testCase.summary)
                }
            }
        }
    }
})
