package kotlinx.ast.parser.antlr.kotlin

import kotlinx.ast.common.AstSource
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import java.nio.file.FileSystems
import java.nio.file.Files

internal actual fun AstSource.toAntlrKotlinCharStream(): CharStream {
    return when (this) {
        is AstSource.File -> {
            /*
            // There is a Bug in CharStreams.fromFileName:
            // https://github.com/Strumenta/antlr-kotlin/issues/38
            // https://github.com/kotlinx/ast/issues/10
            CharStreams.fromFileName(filename)
            // Workaround:
            // first reading the file into a string
            */
            val path = FileSystems.getDefault().getPath(filename)
            val content = Files.readString(path)
            CharStreams.fromString(content)
        }
        is AstSource.String ->
            CharStreams.fromString(content)
    }
}
