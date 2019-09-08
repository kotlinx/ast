package kotlinx.ast.common

sealed class Ast {
    data class Terminal(
        val text: String,
        val channel: AstChannel
    ) : Ast()

    data class Node(
        val name: String,
        val children: List<Ast>
    ) : Ast()
}
