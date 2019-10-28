package kotlinx.ast.common.impl

import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.DefaultAstNode
import kotlinx.ast.common.ast.DefaultAstTerminal

fun Ast.flatten(defaultChannel: AstChannel): List<Ast> {
    return if (this is DefaultAstNode) {
        val start = mutableListOf<Ast>()
        val self = mutableListOf<Ast>()
        val stop = mutableListOf<Ast>()
        children.forEach { ast ->
            val flat = ast is DefaultAstTerminal && ast.channel != defaultChannel
            if (self.isEmpty()) {
                if (flat) {
                    start += ast
                } else {
                    self += ast
                }
            } else {
                if (flat) {
                    stop += ast
                } else {
                    self += stop
                    stop.clear()
                    self += ast
                }
            }
        }
        start + copy(children = self) + stop
    } else {
        listOf(this)
    }
}
