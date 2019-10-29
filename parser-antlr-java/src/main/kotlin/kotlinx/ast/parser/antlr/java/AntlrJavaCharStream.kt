package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstSource
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams

internal fun AstSource.toAntlrJavaCharStream(): CharStream {
    return when (this) {
        is AstSource.File ->
            CharStreams.fromFileName(filename)
        is AstSource.String ->
            CharStreams.fromString(content)
    }
}
