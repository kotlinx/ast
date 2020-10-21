package kotlinx.ast.common.filter

import kotlinx.ast.common.ast.Ast

interface TreeFilter {
    fun matches(ast: Ast): Boolean

    operator fun <A : Ast> invoke(ast: List<A>): List<A> {
        return ast.filter(::matches)
    }

    infix fun and(other: TreeFilter): TreeFilter {
        return and(this, other)
    }

    infix fun or(other: TreeFilter): TreeFilter {
        return or(this, other)
    }

    operator fun not(): TreeFilter {
        return not(this)
    }
}
