package kotlinx.ast.kotlin

import io.kotlintest.fail
import io.kotlintest.specs.FunSpec
import kotlinx.ast.common.AstFailure
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.AstSuccess
import java.io.File

abstract class DirectoryTest(files: Map<File, String>) : FunSpec({
    if (files.isEmpty()) {
        test("no test data found!") {
            fail("no test data found!")
        }
    }

    files.forEach() { (file, content) ->
        test(file.toString()) {
            when (val summary = KotlinAstParser.parseKotlinFile(AstSource.String(content)).summary()) {
                is AstSuccess -> {
                    // test succeeded
                }
                is AstFailure -> {
                    fail(summary.errors.joinToString("\n"))
                }
                else ->
                    fail("unexpected AstResult $summary")
            }
        }
    }
})
