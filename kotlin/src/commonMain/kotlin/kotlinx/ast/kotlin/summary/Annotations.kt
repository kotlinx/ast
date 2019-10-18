package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.klass.KlassAnnotation
import kotlinx.ast.common.klass.KlassDeclaration
import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val annotationsMapper: TreeMapMapper = TreeMapMapper()
    .flattenNames("unescapedAnnotation")
    .filterChildren(
        flatten = true,
        childrenNamesToKeep = mapOf(
            "singleAnnotation" to setOf("unescapedAnnotation")
        )
    ).map("annotation") { node: AstNode ->
        treeMap(node.children).map { summary ->
            TreeMapResult.Continue(
                KlassAnnotation(
                    identifier = summary.filterIsInstance<KlassIdentifier>(),
                    arguments = summary.filterIsInstance<KlassDeclaration>()
                )
            )
        }
    }
