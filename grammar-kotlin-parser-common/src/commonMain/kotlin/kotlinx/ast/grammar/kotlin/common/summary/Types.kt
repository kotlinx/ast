package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.astFailure
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
            "nullableType" to setOf(
                "typeReference",
                "parenthesizedType",
                "quest"
            ),
            "valueArguments" to setOf("valueArgument"),
            "identifier" to setOf("simpleIdentifier"),
            "typeArguments" to setOf("typeProjection"),
            "userType" to setOf("simpleUserType")
        )
    ).mapChildren("simpleIdentifier") {
        it.map { a: AstTerminal ->
            TreeMapResult.Continue(KlassIdentifier(a, a.text))
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
            treeMap(type).flatMap { list ->
                list.fold(
                    TreeMapResult.Keep as AstResult<TreeMapResult>
                ) { result, right ->
                    when {
                        result is TreeMapResult.Keep ->
                            TreeMapResult.Continue(right)
                        result is TreeMapResult.Continue && result.list.size == 1 -> {
                            val left = result.list.first()
                            when {
                                left is KlassIdentifier && right is KlassIdentifier ->
                                    TreeMapResult.Continue(
                                        left.copy(
                                            identifier = listOf(left.identifier, right.identifier).joinToString(".")
                                        )
                                    )
                                left is KlassIdentifier && right.description == "quest" ->
                                    TreeMapResult.Continue(
                                        left.copy(nullable = true)
                                    )
                                else ->
                                    astFailure("found unsupported ast node '$right' in typeProjection!")
                            }
                        }
                        else ->
                            astFailure("found unsupported ast node '$right' in typeProjection!")
                    }
                }
            }
        }.map { _/*TODO typeProjectionModifiers*/: Ast, type: KlassIdentifier ->
            treeMap(type).map(TreeMapResult::Continue)
        }
    }
