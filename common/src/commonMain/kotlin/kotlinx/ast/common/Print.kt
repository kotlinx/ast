package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstTerminal

fun String.escape(): String {
    return replace("\n", "\\n")
        .replace("\t", "\\t")
}

fun Ast.print() {
    print(0, ::println)
}

private fun Ast.print(depth: Int, print: (String) -> Unit) {
    val space = " ".repeat(depth * 2)
    val text = if (this is AstTerminal) {
        val channel = if (this is DefaultAstTerminal) {
            " (${channel.name})"
        } else {
            ""
        }
        val value = text.escape()
        "$description >>>$value<<<$channel"
    } else {
        description
    }
    print("$space$text")

    if (this is AstNode) {
        children.forEach { child ->
            child.print(depth + 1, print)
        }
    }
}

fun Ast.printString(): String {
    var actual = ""
    print(0) { line ->
        actual += line
        actual += "\n"
    }
    return actual
}
