package kotlinx.ast.kotlin

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

private val path = listOf(
    Path.of("src/commonTest/resources/testdata"),
    Path.of("kotlin/src/commonTest/resources/testdata")
).find { path ->
    Files.isDirectory(path)
}

data class TestData(
    val name: String,
    val kotlinFile: File,
    val kotlinContent: String,
    val rawAstFile: File,
    val rawAstContent: String?,
    val summaryFile: File,
    val summaryContent: String?
)

private fun File.sourceFile(suffix: String): File {
    return File(parentFile, name.replace(".kt.txt", suffix))
}

private fun File.readTextOrNull(): String? {
    return if (isFile) {
        readText()
    } else {
        null
    }
}

internal fun testData(): List<TestData> {
    if (path == null) {
        return emptyList()
    }
    return Files.list(path).toList().map(Path::toFile).filter { file ->
        file.name.endsWith(".kt.txt")
    }.mapNotNull { kotlinFile ->
        val kotlinContent = kotlinFile.readTextOrNull()
        if (kotlinContent == null) {
            null
        } else {
            val rawAstFile = kotlinFile.sourceFile(".raw.txt")
            val summaryFile = kotlinFile.sourceFile(".summary.txt")
            TestData(
                name = kotlinFile.nameWithoutExtension,
                kotlinFile = kotlinFile,
                kotlinContent = kotlinContent,
                rawAstFile = rawAstFile,
                rawAstContent = rawAstFile.readTextOrNull(),
                summaryFile = summaryFile,
                summaryContent = summaryFile.readTextOrNull()
            )
        }
    }
}
