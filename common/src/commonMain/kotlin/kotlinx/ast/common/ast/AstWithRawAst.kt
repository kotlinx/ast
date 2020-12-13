package kotlinx.ast.common.ast

import kotlinx.ast.common.klass.RawAst

interface AstWithRawAst : Ast {
    val raw: RawAst?

    fun detachRaw(): Ast
}
