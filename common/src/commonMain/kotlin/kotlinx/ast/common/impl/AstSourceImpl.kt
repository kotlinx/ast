package kotlinx.ast.common.impl

import kotlinx.ast.common.AstSource
import org.antlr.v4.kotlinruntime.CharStream

internal expect fun AstSource.toCharStream(): CharStream
