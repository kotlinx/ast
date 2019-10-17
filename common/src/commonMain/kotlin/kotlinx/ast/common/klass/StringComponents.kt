package kotlinx.ast.common.klass

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstGroup
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.escape

sealed class StringComponent() : AstGroup

data class StringComponentRaw(
    val string: String
) : StringComponent() {
    override val description: String = """"${string.escape()}""""
}

fun String.asStringComponentRaw(): StringComponent {
    return StringComponentRaw(this)
}

fun <A : Ast> A.asStringComponent(): StringComponent {
    return if (this is AstNode) {
        StringComponentAstNodeExpression(this)
    } else {
        StringComponentAstExpression(this)
    }
}

sealed class StringComponentExpression<A : Ast>() : StringComponent() {
    abstract val expression: A
}

data class StringComponentAstExpression(
    override val expression: Ast
) : StringComponentExpression<Ast>(), Ast by expression

data class StringComponentAstNodeExpression(
    override val expression: AstNode
) : StringComponentExpression<AstNode>(), AstNode by expression
