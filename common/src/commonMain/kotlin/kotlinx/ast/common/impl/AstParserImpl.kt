package kotlinx.ast.common.impl

import kotlinx.ast.common.Ast
import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.AstSource
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode

private class AstParserImpl(
    private val stream: CommonTokenStream,
    private val parserNames: Array<String>,
    private val channels: Array<AstChannel>
) {
    private val consumedHiddenTokens = mutableSetOf<Token>()
    private val defaultChannel = channels.first()

    private fun toAstTerminal(token: Token): Ast.Terminal {
        val text = token.text ?: throw RuntimeException()
        val channel = channels[token.channel]
        return Ast.Terminal(text, channel)
    }

    private fun hiddenTokens(node: ParseTree, start: Boolean): List<Ast.Terminal> {
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

    private fun hiddenTokens(token: Token?, left: Boolean): List<Ast.Terminal> {
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
                Ast.Node(name, children)
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
    extractor: (P) -> ParseTree,
    lexerFactory: (CharStream) -> Lexer,
    parserFactory: (TokenStream) -> P
): Ast {
    val input = source.toCharStream()
    val lexer = lexerFactory(input)
    val stream = CommonTokenStream(lexer)
    val parser = parserFactory(stream)
    val subject = extractor(parser)
    val ruleNames = parser.ruleNames ?: emptyArray()
    val channelNames = lexer.channelNames ?: emptyArray()
    val channels = channelNames.withIndex().map { (i, channel) ->
        AstChannel(i, channel)
    }.toTypedArray()
    return AstParserImpl(stream, ruleNames, channels).parse(subject).join()
}
