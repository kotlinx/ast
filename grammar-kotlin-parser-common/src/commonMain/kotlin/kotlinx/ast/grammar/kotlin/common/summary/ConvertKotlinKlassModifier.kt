package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.filter.TreeFilter
import kotlinx.ast.common.klass.KlassModifier
import kotlinx.ast.common.klass.KlassModifierGroup
import kotlinx.ast.common.map.TreeMapBuilder

fun <State> TreeMapBuilder<State>.convertKotlinKlassModifier(filter: TreeFilter): TreeMapBuilder<State> {
    return convert(filter = filter) { node: AstNode ->
        val group = KlassModifierGroup(node.description)
        astSuccess(
            node.children.filterIsInstance<AstTerminal>().map { modifier ->
                KlassModifier(
                    modifier.text,
                    group,
                    raw = attachRaw(modifier)
                )
            }
        )
    }
}
