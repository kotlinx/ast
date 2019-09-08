package kotlinx.ast.common

import kotlinx.ast.common.impl.parse
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree

abstract class AstParser<P : Parser>(
    private val lexerFactory: (CharStream) -> Lexer,
    private val parserFactory: (TokenStream) -> P
) {
    protected fun parse(source: AstSource, extractor: (P) -> ParseTree): Ast {
        return parse(source, extractor, lexerFactory, parserFactory)
    }
}
