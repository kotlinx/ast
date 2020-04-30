package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.astFailure
import kotlinx.ast.common.filter
import kotlinx.ast.common.klass.*
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

fun List<Ast>.expressions(): List<Ast> {
    return filter("expression").filterIsInstance<AstNode>().flatMap(AstNode::children)
}

val declarationsMapper: TreeMapMapper = TreeMapMapper()
    .excludeNames<AstNode>("functionBody")
    .flattenNames(
        "constructorInvocation",
        "classMemberDeclaration",
        "classMemberDeclarations",
        "classParameters",
        "declaration",
        "variableDeclaration",
        "delegationSpecifier",
        "propertyDelegate"
    ).filterChildren(
        flatten = true,
        childrenNamesToKeep = mapOf(
            "valueArguments" to setOf("valueArgument"),
            "classParameters" to setOf("classParameter"),
            "typeParameters" to setOf("typeParameter"),
            "delegationSpecifiers" to setOf("annotatedDelegationSpecifier"),
            "parameter" to setOf(
                "simpleIdentifier",
                "type"
            ),
            "functionValueParameters" to setOf(
                "functionValueParameter"
            )
        )
    ).filterChildren(
        flatten = false,
        childrenNamesToKeep = mapOf(
            "typeParameter" to setOf(
                "simpleIdentifier",
                "type"
            ),
            "functionValueParameter" to setOf(
                "parameterModifiers",
                "parameter",
                "expression"
            ),
            "delegationSpecifiers" to setOf(
                "annotatedDelegationSpecifier"
            ),
            "classBody" to setOf(
                "classMemberDeclarations"
            )
        ),
        childrenNamesToDrop = mapOf(
            "classDeclaration" to setOf(
                "COLON"
            ),
            "classParameter" to setOf(
                "COLON"
            ),
            "functionDeclaration" to setOf(
                "FUN",
                "COLON"
            )
        )
    ).map("valueArgument") { node: AstNode ->
        treeMap(node.children).flatMap { summary ->
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = "argument",
                    identifier = summary.filterIsInstance<KlassIdentifier>().firstOrNull(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    expressions = summary.expressions()
                )
            )
        }
    }.map("primaryConstructor") { node: AstNode ->
        treeMap(node.children).flatMap { summary ->
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = "constructor",
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassDeclaration>()
                )
            )
        }
    }.map("classParameter") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val keyword = when {
                summary.filterIsInstance<AstTerminal>().map(AstTerminal::description).contains("VAR") ->
                    "var"
                summary.filterIsInstance<AstTerminal>().map(AstTerminal::description).contains("VAL") ->
                    "val"
                else ->
                    "parameter"
            }

            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = keyword,
                    identifier = identifier[0],
                    type = identifier.getOrNull(1),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    expressions = summary.expressions()
                )
            )
        }
    }.map("propertyDeclaration") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val keyword = if (summary.filterIsInstance<AstTerminal>()
                    .map(AstTerminal::description).contains("VAR")
            ) {
                "var"
            } else {
                "val"
            }

            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = keyword,
                    identifier = identifier.firstOrNull(),
                    type = identifier.getOrNull(1),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    expressions = summary.expressions()
                )
            )
        }
    }.map("functionValueParameter") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = "parameter",
                    identifier = identifier.first(),
                    type = identifier.getOrNull(1),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    expressions = summary.expressions()
                )
            )
        }
    }.map("annotatedDelegationSpecifier") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            if (identifier.size == 1) {
                TreeMapResult.Continue(
                    KlassInheritance(
                        raw = node,
                        type = identifier.first(),
                        annotations = summary.filterIsInstance<KlassAnnotation>()
                    )
                )
            } else {
                TreeMapResult.Continue(summary)
            }
        }
    }.map("typeParameter") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val result = if (summary.size == 1 || summary.size == 2) {
                val a = summary[0] as? KlassIdentifier
                val b = summary.getOrNull(1) as? KlassIdentifier
                if (a != null) {
                    TreeMapResult.Continue(
                        KlassTypeParameter(node, a, b)
                    )
                } else {
                    null
                }
            } else {
                null
            }
            result ?: TreeMapResult.Failure(astFailure<Nothing>("failed to parse typeParameter"))
        }
    }.map("functionDeclaration") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = "fun",
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassDeclaration>(),
                    typeParameters = summary.filterIsInstance<KlassTypeParameter>(),
                    type = identifier.getOrNull(1)
                )
            )
        }
    }.map("classDeclaration") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            val keyword = if (summary.filter("INTERFACE").isEmpty()) {
                "class"
            } else {
                "interface"
            }
            val expressions = summary.filter(
                setOf(
                    "primaryConstructor",
                    "typeConstraints",
                    "classBody",
                    "enumClassBody"
                )
            )
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = keyword,
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassDeclaration>(),
                    typeParameters = summary.filterIsInstance<KlassTypeParameter>(),
                    type = identifier.getOrNull(1),
                    inheritance = summary.filterIsInstance<KlassInheritance>(),
                    expressions = expressions
                )
            )
        }
    }.map("objectDeclaration") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassDeclaration(
                    raw = node,
                    keyword = "object",
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassDeclaration>(),
                    typeParameters = summary.filterIsInstance<KlassTypeParameter>(),
                    inheritance = summary.filterIsInstance<KlassInheritance>(),
                    type = identifier.getOrNull(1)
                )
            )
        }
    }
