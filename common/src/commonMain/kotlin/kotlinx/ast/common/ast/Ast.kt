package kotlinx.ast.common.ast

interface Ast {
    val description: String
}

interface AstSelfTyped<Self : Ast> : Ast
