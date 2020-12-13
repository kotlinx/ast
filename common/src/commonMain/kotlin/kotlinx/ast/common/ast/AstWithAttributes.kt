package kotlinx.ast.common.ast

interface AstWithAttributes : Ast {
    val attributes: List<Ast>
}
