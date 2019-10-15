package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.impl.parse
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.TokenStream

abstract class AstParser<P : Parser>(
    private val lexerFactory: (CharStream) -> Lexer,
    private val parserFactory: (TokenStream) -> P
) {
    fun parse(source: AstSource, type: AstParserType<P>): Ast {
        return parse(source, type, lexerFactory, parserFactory)
    }

    fun parse(source: AstSource, types: List<AstParserType<P>>): List<Ast> {
        return parse(source, types, lexerFactory, parserFactory)
    }
}
