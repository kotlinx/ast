package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

object TreeFilterAll : TreeFilter {
    override fun matches(ast: Ast): Boolean {
        return true
    }
}
