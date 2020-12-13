package kotlinx.ast.common.klass

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstAttachments
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstSelfTypedWithExtensions
import kotlinx.ast.common.escape

sealed class StringComponent() : AstSelfTypedWithExtensions<StringComponent>

data class StringComponentRaw(
    val string: String,
    override val attachments: AstAttachments = AstAttachments(),
) : StringComponent() {
    override val description: String = """"${string.escape()}""""

    override fun withAttachments(attachments: AstAttachments): StringComponentRaw {
        return copy(attachments = attachments)
    }
}

fun String.asStringComponentRaw(
    attachments: AstAttachments = AstAttachments()
): StringComponent {
    return StringComponentRaw(this, attachments)
}

data class StringComponentEscape(
    val escape: String,
    override val attachments: AstAttachments = AstAttachments(),
) : StringComponent() {
    override val description: String = """Escape("${escape.escape()}")"""

    override fun withAttachments(attachments: AstAttachments): StringComponentEscape {
        return copy(attachments = attachments)
    }
}

fun String.asStringComponentEscape(): StringComponent {
    return StringComponentEscape(this)
}

fun <A : Ast> A.asStringComponent(
    attachments: AstAttachments = AstAttachments()
): StringComponent {
    return if (this is AstNode) {
        StringComponentAstNodeExpression(this, attachments)
    } else {
        StringComponentAstExpression(this, attachments)
    }
}

sealed class StringComponentExpression<A : Ast>() : StringComponent() {
    abstract val expression: A
}

data class StringComponentAstExpression(
    override val expression: Ast,
    override val attachments: AstAttachments = AstAttachments(),
) : StringComponentExpression<Ast>(), Ast by expression {
    override fun withAttachments(attachments: AstAttachments): StringComponentAstExpression {
        return copy(attachments = attachments)
    }
}

data class StringComponentAstNodeExpression(
    override val expression: AstNode,
    override val attachments: AstAttachments = AstAttachments(),
) : StringComponentExpression<AstNode>(), AstNode by expression {
    override fun withAttachments(attachments: AstAttachments): StringComponentAstNodeExpression {
        return copy(attachments = attachments)
    }
}
