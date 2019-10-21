package kotlinx.ast.kotlin

import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.printString
import java.io.File

class TestDataTest : FunSpec({
    val tests = testData()

    if (tests.isEmpty()) {
        test("no test data found!") {
            fail("no test data found!")
        }
    }

    tests.forEach() { testData ->
        testData.apply {
            context(name) {
                val overwriteTestData = System.getProperty("overwrite.test.data") != null
                val ast by lazy { KotlinAstParser.parseKotlinFile(AstSource.String(kotlinContent)) }

                suspend fun ContextScope.test(
                    testCase: String,
                    expected: String?,
                    expectedFile: File,
                    execute: () -> String
                ) {
                    test(testCase) {
                        val actual = execute()
                        if (expected == null) {
                            expectedFile.writeText(actual)
                            fail("expected data for test \"$name -- $testCase\" not found, data created, please restart the test!")
                        } else {
                            if (overwriteTestData && actual != expected) {
                                expectedFile.writeText(actual)
                                fail("expected data for test \"$name -- $testCase\" differs, data updated!")
                            } else {
                                actual shouldBe expected
                            }
                        }
                    }
                }

                test("raw ast", rawAstContent, rawAstFile) {
                    ast.printString()
                }

                test("summary ast", summaryContent, summaryFile) {
                    ast.summary().map {
                        it.joinToString("", transform = Ast::printString)
                    }.get()
                }
            }
        }
    }
})
