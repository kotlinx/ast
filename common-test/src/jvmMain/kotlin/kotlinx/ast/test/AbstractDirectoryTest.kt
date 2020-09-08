package kotlinx.ast.test

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import kotlinx.ast.common.AstSource
import java.io.File

abstract class AbstractDirectoryTest(
    tester: FunSpec.(File, AstSource) -> Unit,
    files: Map<File, String>
) : FunSpec({
    if (files.isEmpty()) {
        test("no test data found!") {
            fail("no test data found!")
        }
    }

    files.forEach() { (file, content) ->
        tester(file, AstSource.String(content))
    }
})
