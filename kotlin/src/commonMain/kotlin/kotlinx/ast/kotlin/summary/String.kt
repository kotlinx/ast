package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstNode
import kotlinx.ast.common.astFailure
import kotlinx.ast.common.klass.*
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val stringMapper: TreeMapMapper = TreeMapMapper()
    .flattenNames(
        "lineStringContent",
        "multiLineStringContent"
    ).filterChildren(
        flatten = true,
        childrenNamesToKeep = mapOf(
            "lineStringLiteral" to setOf(
                "lineStringContent",
                "lineStringExpression"
            ),
            "multiLineStringLiteral" to setOf(
                "multiLineStringContent",
                "multiLineStringExpression"
            )
        )
    ).filterChildren(
        flatten = false,
        childrenNamesToKeep = mapOf(
            "lineStringExpression" to setOf("expression"),
            "multiLineStringExpression" to setOf("expression")
        )
    ).map(
        setOf(
            "LineStrText",
            "MultiLineStringQuote",
            "MultiLineStrText"
        )
    ) { terminal: AstTerminal ->
        TreeMapResult.Continue(
            terminal.text.asStringComponentRaw()
        )
    }.map("LineStrEscapedChar") { terminal: AstTerminal ->
        TreeMapResult.Continue(
            terminal.text.asStringComponentEscape()
        )
    }.map(
        setOf(
            "LineStrRef",
            "MultiLineStrRef"
        )
    ) { terminal: AstTerminal ->
        TreeMapResult.Continue(
            KlassIdentifier(terminal.text.drop(1)).asStringComponent()
        )
    }.map(
        setOf(
            "lineStringExpression",
            "multiLineStringExpression"
        )
    ) { expression: DefaultAstNode ->
        treeMap(expression.children).flatMap { summary ->
            if (summary.size == 1) {
                TreeMapResult.Continue(
                    summary.first().asStringComponent()
                )
            } else {
                astFailure<TreeMapResult>("found ${expression.description} with ${summary.size} expressions!")
            }
        }
    }.mapChildren("stringLiteral", treeMap = true) {
        it.mapList { components: List<StringComponent> ->
            TreeMapResult.Continue(KlassString(components))
        }
    }
