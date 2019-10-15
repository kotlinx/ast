package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.filter
import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val globalsMapper: TreeMapMapper = TreeMapMapper()
    .flattenNames(
        "importAlias",
        "kotlinFile",
        "topLevelObject"
    ).filterChildren(
        flatten = false,
        childrenNamesToKeep = mapOf(
            "packageHeader" to setOf(
                "identifier"
            ),
            "importHeader" to setOf(
                "identifier",
                "importAlias",
                "MULT",
                "AS"
            )
        )
    ).treeMap("packageHeader") { identifier: List<KlassIdentifier> ->
        TreeMapResult.Continue(
            PackageHeader(identifier)
        )
    }.treeMap("importHeader") { children: List<Ast> ->
        val identifier = children.filterIsInstance<KlassIdentifier>()
        TreeMapResult.Continue(
            when {
                children.filter("MULT").isNotEmpty() ->
                    Import(
                        identifier = identifier,
                        starProjection = true
                    )
                children.filter("AS").isNotEmpty() -> {
                    Import(
                        identifier = identifier.dropLast(1),
                        alias = identifier.last()
                    )
                }
                else ->
                    Import(identifier)
            }
        )
    }
