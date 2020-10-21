package kotlinx.ast.common.ast

import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.AstResult
import kotlinx.ast.common.filter.TreeFilter
import kotlinx.ast.common.klass.RawAst
import kotlinx.ast.common.map.TreeMapContext

interface Ast {
    val description: String
}

interface AstTerminal : Ast {
    val text: String

    companion object {
        operator fun <T> invoke(
            description: String,
            text: String,
            channel: AstChannel
        ): AstTerminal {
            return DefaultAstTerminal(description, text, channel)
        }
    }
}

data class DefaultAstTerminal(
    override val description: String,
    override val text: String,
    val channel: AstChannel
) : AstTerminal

interface AstNode : Ast {
    val children: List<Ast>

    fun <State> TreeMapContext<State>.filterChildren(filter: TreeFilter): AstResult<State, AstNode>

    fun <State> TreeMapContext<State>.mapChildren(
        mapper: TreeMapContext<State>.(Ast) -> AstResult<State, List<Ast>>
    ): AstResult<State, AstNode>

    fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, AstNode>
}

interface AstNodeDefaults<Self : AstNode> : AstNode {
    override fun <State> TreeMapContext<State>.filterChildren(filter: TreeFilter): AstResult<State, Self> {
        return mapChildren { ast ->
            if (filter.matches(ast)) {
                astContinueList(ast)
            } else {
                astDrop()
            }
        }
    }

    override fun <State> TreeMapContext<State>.mapChildren(
        mapper: TreeMapContext<State>.(Ast) -> AstResult<State, List<Ast>>
    ): AstResult<State, Self> {
        val attributes = if (this is AstWithAttributes) {
            this.attributes
        } else {
            emptyList()
        }
        return (attributes + children).astFold(mapper).flatMap { result ->
            withChildren(result)
        }
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, Self>

    companion object {
        operator fun invoke(
            description: String,
            children: List<Ast>
        ): AstNode {
            return DefaultAstNode(description, children)
        }
    }
}

data class DefaultAstNode(
    override val description: String,
    override val children: List<Ast>
) : AstNodeDefaults<AstNode> {
    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, DefaultAstNode> {
        return astContinue(copy(children = children))
    }
}

interface AstGroup : Ast

interface AstWithAttributes : Ast {
    val attributes: List<Ast>
}

interface AstWithRaw : Ast {
    val raw: RawAst?

    fun detachRaw(): Ast
}
