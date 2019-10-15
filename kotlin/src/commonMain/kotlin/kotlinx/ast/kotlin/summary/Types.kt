package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.klass.starProjection
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val typesMapper: TreeMapMapper = TreeMapMapper()
    .flattenNames(
        "type",
        "typeReference"
    ).filterChildren(
        flatten = true,
        childrenNamesToKeep = mapOf(
            "valueArguments" to setOf("valueArgument"),
            "identifier" to setOf("simpleIdentifier"),
            "typeArguments" to setOf("typeProjection"),
            "userType" to setOf("simpleUserType")
        )
    ).mapChildren("simpleIdentifier") {
        it.map { a: AstTerminal ->
            TreeMapResult.Continue(KlassIdentifier(a.text))
        }
    }.mapChildren("simpleUserType") {
        it.mapList { children: List<Ast> ->
            treeMap(children).map { list ->
                val identifier = list.reduce { identifier, parameter ->
                    when {
                        identifier is KlassIdentifier && parameter is KlassIdentifier ->
                            identifier.parameterizedBy(parameter)
                        else ->
                            identifier
                    }
                }
                TreeMapResult.Continue(identifier)
            }
        }
    }.mapChildren("typeProjection") {
        it.map<AstTerminal> {
            TreeMapResult.Continue(starProjection)
        }.map { type: AstNode ->
            treeMap(type).map { list ->
                val identifier = list.filterIsInstance<KlassIdentifier>()
                    .reduce { left, right ->
                        left.copy(
                            identifier = listOf(left.identifier, right.identifier).joinToString(".")
                        )
                    }
                TreeMapResult.Continue(identifier)
            }
        }.map { _/*TODO typeProjectionModifiers*/: Ast, type: KlassIdentifier ->
            treeMap(type).map(TreeMapResult::Continue)
        }
    }
