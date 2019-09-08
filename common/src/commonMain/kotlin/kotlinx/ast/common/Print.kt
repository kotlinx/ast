package kotlinx.ast.common

fun Ast.print(depth: Int = 0) {
    val space = " ".repeat(depth * 2)
    val text = when (this) {
        is Ast.Terminal -> {
            val value = text
                .replace("\n", "\\n")
                .replace("\t", "\\t")
            val name = channel.name
            ">>>$value<<< ($name)"
        }
        is Ast.Node ->
            name
    }
    println("$space $text")

    if (this is Ast.Node) {
        children.forEach { child ->
            child.print(depth + 1)
        }
    }
}
