package kotlinx.ast.common

import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.tree.ParseTree

interface AstParserType<P : Parser> {
    fun extract(parser: P): ParseTree
}
