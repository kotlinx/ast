package kotlinx.ast.test

import java.io.File

object OverwriteTestData {
    private val overrideTestData: Boolean by lazy { overrideTestData(File(".").absoluteFile) }

    operator fun invoke(): Boolean {
        return overrideTestData
    }

    private tailrec fun overrideTestData(base: File?): Boolean {
        return when {
            base == null ->
                false
            File(base, ".override-test-data").exists() ->
                true
            else ->
                overrideTestData(base.parentFile)
        }
    }
}
