package kotlinx.ast.parser.antlr.kotlin

import kotlinx.ast.common.AstSource
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams

internal actual fun AstSource.toAntlrKotlinCharStream(): CharStream {
    return when (this) {
        is AstSource.File ->
            CharStreams.fromFileName(filename)
        is AstSource.String ->
            CharStreams.fromString(content)
    }
}
