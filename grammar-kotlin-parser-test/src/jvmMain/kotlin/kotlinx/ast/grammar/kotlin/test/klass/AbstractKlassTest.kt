package kotlinx.ast.grammar.kotlin.test.klass

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.flatten
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

            test("print") {
                val actual = given.joinToString(
                    separator = "",
                    transform = Ast::printString
                )
                check(actual, testCase.print + "\n")
            }

            test("summary") {
                val actual = given.map(Ast::summary).flatten().get().flatten()
                check(actual, testCase.summary)
            }
        }
    }
})
