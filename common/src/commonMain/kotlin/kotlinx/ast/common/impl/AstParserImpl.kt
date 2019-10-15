package kotlinx.ast.common.impl

import kotlinx.ast.common.*
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstNode
import kotlinx.ast.common.ast.DefaultAstTerminal
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode

private class AstParserImpl(
    private val stream: CommonTokenStream,
    private val parserNames: Array<String>,
    private val tokenNames: Array<String?>,
    private val channels: Array<AstChannel>
) {
    private val consumedHiddenTokens = mutableSetOf<Token>()
    private val defaultChannel = channels.first()

    private fun toAstTerminal(token: Token): AstTerminal {
        val text = token.text ?: throw RuntimeException()
        val name = if (token.type == -1) "EOF" else tokenNames[token.type] ?: "<Invalid>"
        val channel = channels[token.channel]
        return DefaultAstTerminal(name, text, channel)
    }

    private fun hiddenTokens(node: ParseTree, start: Boolean): List<AstTerminal> {
        return when (node) {
            is ParserRuleContext -> {
                val token = if (start) {
                    node.start
                } else {
                    node.stop
                }
                hiddenTokens(token, start)
            }
            is TerminalNode ->
                hiddenTokens(node.symbol, start)
            else ->
                emptyList()
        }
    }

    private fun hiddenTokens(token: Token?, left: Boolean): List<AstTerminal> {
        val index = token?.tokenIndex
        val list = when {
            index == null ->
                null
            left ->
                stream.getHiddenTokensToLeft(index)
            else ->
                stream.getHiddenTokensToRight(index)
        } ?: emptyList()

        val result = list.filterNot(consumedHiddenTokens::contains)
        result.forEach {
            consumedHiddenTokens.add(it)
        }

        return result.map(::toAstTerminal)
    }

    fun parse(node: ParseTree): AstList {
        val start = hiddenTokens(node, start = true)
        val stop = hiddenTokens(node, start = false)
        val ast = when (node) {
            is ParserRuleContext -> {
                val name = parserNames[node.ruleIndex]
                val children = (node.children ?: emptyList<ParseTree>()).flatMap { children ->
                    parse(children).flatten().map { ast ->
                        ast.flatten(defaultChannel)
                    }.flatten()
                }
                DefaultAstNode(name, children)
            }
            is TerminalNode -> {
                toAstTerminal(node.symbol ?: throw RuntimeException())
            }
            else ->
                throw RuntimeException()
        }
        return AstList(start, ast, stop)
    }
}

fun <P : Parser> parse(
    source: AstSource,
    type: AstParserType<P>,
    lexerFactory: (CharStream) -> Lexer,
    parserFactory: (TokenStream) -> P
): Ast {
    val result = parse(source, listOf(type), lexerFactory, parserFactory)
    if (result.size != 1) {
        throw RuntimeException("expected exactly one ast!")
    }
    return result.first()
}

fun <P : Parser> parse(
    source: AstSource,
    types: List<AstParserType<P>>,
    lexerFactory: (CharStream) -> Lexer,
    parserFactory: (TokenStream) -> P
): List<Ast> {
    val input = source.toCharStream()
    val lexer = lexerFactory(input)
    val stream = CommonTokenStream(lexer)
    val parser = parserFactory(stream)
    val ruleNames = parser.ruleNames ?: emptyArray()
    val channelNames = lexer.channelNames ?: emptyArray()
    val channels = channelNames.withIndex().map { (i, channel) ->
        AstChannel(i, channel)
    }.toTypedArray()
    val vocabulary = lexer.vocabulary
    val tokenNames = Array(vocabulary.maxTokenType) {
        vocabulary.getSymbolicName(it)
    }
    val astParser = AstParserImpl(stream, ruleNames, tokenNames, channels)
    val result = types.mapNotNull { type ->
        if (type.toString() == "token") {
            val token = stream.LT(1)
            if (token == null) {
                null
            } else {
                if (token.type != -1) {
                    stream.consume()
                }
                DefaultAstTerminal(tokenNames[token.type] ?: "", token.text ?: "", channels[token.channel])
            }
        } else {
            astParser.parse(type.extract(parser)).join()
        }
    }
    if (stream.LA(1) != -1) {
        throw RuntimeException("trailing data while parsing types \"${types.joinToString("\", \"")}\"")
    }
    return result
}
