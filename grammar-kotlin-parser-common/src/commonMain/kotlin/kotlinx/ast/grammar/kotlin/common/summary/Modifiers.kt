package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.klass.KlassModifier
import kotlinx.ast.common.klass.KlassModifierGroup
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val modifierMapper: TreeMapMapper = TreeMapMapper()
    .flattenNames(
        "modifier",
        "modifiers",
        "parameterModifiers"
    ).mapChildren(
        names = setOf(
            "classModifier",
            "memberModifier",
            "visibilityModifier",
            "functionModifier",
            "propertyModifier",
            "inheritanceModifier",
            "parameterModifier",
            "platformModifier"
        )
    ) {
        it.map { modifier: AstTerminal ->
            val group = KlassModifierGroup(context.description)
            TreeMapResult.Continue(KlassModifier(modifier.text, group, raw = attach(modifier)))
        }
    }
