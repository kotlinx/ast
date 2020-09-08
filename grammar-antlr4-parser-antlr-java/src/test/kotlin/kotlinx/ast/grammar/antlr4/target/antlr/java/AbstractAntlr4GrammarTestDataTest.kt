package kotlinx.ast.grammar.antlr4.target.antlr.java

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import kotlinx.ast.common.printString
import kotlinx.ast.grammar.antlr4.common.Antlr4GrammarParser
import kotlinx.ast.test.pathOf
import kotlinx.ast.test.readTextOrNull
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

abstract class AbstractAntlr4GrammarTestDataTest<Parser : Antlr4GrammarParser<*, *>>(
    parser: Parser
) : kotlinx.ast.test.AbstractDirectoryTest({ file, source ->
    val name = file.toString()
    val expectedFile = File(file.absolutePath.toString().replace(".g4.txt", ".raw.txt"))
    val overwriteTestData = System.getProperty("overwrite.test.data") != null
    val ast by lazy { parser.parseGrammarSpec(source) }
    val expected by lazy { expectedFile.readTextOrNull() }

    context("name") {
        test("raw ast") {
            val actual = ast.printString()
            if (expected == null) {
                expectedFile.writeText(actual)
                fail("expected data for test \"$name -- raw ast\" not found, data created, please restart the test!")
            } else {
                if (overwriteTestData && actual != expected) {
                    expectedFile.writeText(actual)
                    fail("expected data for test \"$name -- raw ast\" differs, data updated!")
                } else {
                    actual shouldBe expected
                }
            }
        }
    }
}, listOf(
    pathOf("../grammar-antlr4-parser-test/src/commonMain/resources/testdata"),
    pathOf("grammar-antlr4-parser-test/src/commonMain/resources/testdata")
).find { path ->
    Files.isDirectory(path)
}.let { path ->
    Files.list(path).toList().map(Path::toFile).filter { file ->
        file.name.endsWith(".g4.txt")
    }.mapNotNull { file ->
        val text = file.readTextOrNull()
        if (text == null) {
            null
        } else {
            file to text
        }
    }.toMap()
})
