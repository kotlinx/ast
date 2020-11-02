package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstSource
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException

data class AntlrJavaErrorListener(val filename: String) : BaseErrorListener() {

    constructor(source: AstSource) : this(source.description)

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        throw ParseCancellationException("$filename:$line:$charPositionInLine: $msg")
    }
}
