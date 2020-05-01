package kotlinx.ast.common.map

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.klass.RawAst

interface TreeMapWithRawAst : TreeMap {
    val attachRawAst: Boolean

    fun attach(ast: Ast): RawAst? {
        return if (attachRawAst) {
            RawAst(ast)
        } else {
            null
        }
    }
}
