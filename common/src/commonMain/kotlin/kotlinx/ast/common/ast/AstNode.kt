package kotlinx.ast.common.ast

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.filter.TreeFilter
import kotlinx.ast.common.map.TreeMapContext

interface AstNode : Ast {
    val children: List<Ast>

    fun <State> TreeMapContext<State>.filterChildren(filter: TreeFilter): AstResult<State, AstNode>

    fun <State> TreeMapContext<State>.mapChildren(
        mapper: TreeMapContext<State>.(Ast) -> AstResult<State, List<Ast>>
    ): AstResult<State, AstNode>

    fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, AstNode>
}

interface AstNodeSelfTyped<Self : AstNode> : AstNode, AstSelfTyped<Self> {
    override fun <State> TreeMapContext<State>.filterChildren(filter: TreeFilter): AstResult<State, Self> {
        return mapChildren { ast ->
            if (filter.matches(ast)) {
                astContinue(ast)
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
    override val children: List<Ast>,
    override val attachments: AstAttachments = AstAttachments(),
) : AstNodeSelfTyped<DefaultAstNode>, AstSelfTypedWithAttachments<DefaultAstNode> {
    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, DefaultAstNode> {
        return astSuccess(copy(children = children))
    }

    override fun withAttachments(attachments: AstAttachments): DefaultAstNode {
        return copy(attachments = attachments)
    }
}
