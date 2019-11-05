package kotlinx.ast.test

import io.kotlintest.fail
import io.kotlintest.specs.AbstractFunSpec
import io.kotlintest.specs.FunSpec
import kotlinx.ast.common.AstSource
import java.io.File

abstract class AbstractDirectoryTest(
    tester: AbstractFunSpec.(File, AstSource) -> Unit,
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
