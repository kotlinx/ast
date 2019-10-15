package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode

fun List<Ast>.flattenNode(): List<Ast> {
    return filterIsInstance<AstNode>().flatten()
}

fun List<AstNode>.flatten(): List<Ast> {
    return flatMap(AstNode::children)
}

fun List<Ast>.flatten(name: String): List<AstNode> {
    return flatMap { ast ->
        ast.flatten(name)
    }
}

fun Ast.flatten(name: String): List<AstNode> {
    return when {
        this is AstNode && this.description == name ->
            listOf(this)

        this is AstNode ->
            children.flatMap { ast ->
                ast.flatten(name)
            }

        else ->
            emptyList()
    }
}
