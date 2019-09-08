package kotlinx.ast.common.impl

import kotlinx.ast.common.Ast
import kotlinx.ast.common.AstChannel

internal fun Ast.flatten(defaultChannel: AstChannel): List<Ast> {
    return if (this is Ast.Node) {
        val start = mutableListOf<Ast>()
        val self = mutableListOf<Ast>()
        val stop = mutableListOf<Ast>()
        children.forEach { ast ->
            val flat = ast is Ast.Terminal && ast.channel != defaultChannel
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
