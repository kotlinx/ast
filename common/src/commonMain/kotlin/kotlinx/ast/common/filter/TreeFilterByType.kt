package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlin.reflect.KClass

data class TreeFilterByType(
    val astType: KClass<out Ast>
) : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return astType.isInstance(ast)
    }
}

val byTypeTerminal: TreeFilter = TreeFilterByType(AstTerminal::class)
val byTypeNode: TreeFilter = TreeFilterByType(AstNode::class)
