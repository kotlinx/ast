package kotlinx.ast.common.impl

import kotlinx.ast.common.Ast

internal data class AstList(
    val start: List<Ast>,
    val ast: Ast,
    val stop: List<Ast>
) {
    fun flatten(): List<Ast> {
        return start + ast + stop
    }

    fun join(): Ast {
        return if (start.isEmpty() && stop.isEmpty()) {
            ast
        } else if (ast is Ast.Node) {
            ast.copy(children = start + ast.children + stop)
        } else {
            throw RuntimeException()
        }
    }
}
