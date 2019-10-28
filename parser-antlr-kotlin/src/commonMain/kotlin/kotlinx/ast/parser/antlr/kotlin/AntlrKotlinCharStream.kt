package kotlinx.ast.parser.antlr.kotlin

import kotlinx.ast.common.AstSource
import org.antlr.v4.kotlinruntime.CharStream

internal expect fun AstSource.toAntlrKotlinCharStream(): CharStream
