package kotlinx.ast.test

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

fun pathOf(path: String): Path {
    return FileSystems.getDefault().getPath(path)
}

fun Path.listRecusive(suffix: String): List<File> {
    val file = toFile()
    return when {
        file.isDirectory && (!file.name.startsWith(".") || file.name == ".") ->
            Files.list(this).toList().flatMap { it.listRecusive(suffix) }
        file.isFile && file.name.endsWith(suffix) ->
            listOf(file)
        else ->
            emptyList()
    }
}

fun Path.pathMap(suffix: String): Map<File, String> {
    return toRealPath().listRecusive(suffix).mapNotNull { file ->
        val content = file.readTextOrNull()
        if (content == null) {
            null
        } else {
            file to content
        }
    }.toMap()
}

fun File.readTextOrNull(): String? {
    return if (isFile) {
        readText()
    } else {
        null
    }
}
