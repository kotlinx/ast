package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.astFailure
import kotlinx.ast.common.filter
import kotlinx.ast.common.klass.*
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val declarationsMapper: TreeMapMapper = TreeMapMapper()
    .excludeNames<AstNode>("functionBody")
    .flattenNames(
        "constructorInvocation",
        "classMemberDeclaration",
        "classMemberDeclarations",
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
            val expressions = summary.filter("expression")
            val count = expressions.size
            if (count == 1) {
                val first = expressions.first()
                val expression = if (first is AstNode) {
                    first.children
                } else {
                    listOf(first)
                }
                TreeMapResult.Continue(
                    KlassArgument(
                        annotations = summary.filterIsInstance<KlassAnnotation>(),
                        identifier = summary.filterIsInstance<KlassIdentifier>().firstOrNull(),
                        expression = expression
                    )
                )
            } else {
                astFailure<TreeMapResult>("expected one expression, but found $count")
            }
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
                    keyword = keyword,
                    identifier = identifier.first(),
                    type = identifier.getOrNull(1),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    expressions = summary.filter { ast ->
                        ast is AstNode && ast.description == "expression"
                    }
                )
            )
        }
    }.map("functionValueParameter") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassArgument(
                    identifier = identifier.first(),
                    type = identifier.getOrNull(1),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    expression = summary.filter("expression")
                )
            )
        }
    }.map("annotatedDelegationSpecifier") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val identifier = summary.filterIsInstance<KlassIdentifier>()
            TreeMapResult.Continue(
                KlassInheritance(
                    type = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>()
                )
            )
        }
    }.map("typeParameter") { node: AstNode ->
        treeMap(node.children).map { summary ->
            val result = if (summary.size == 1 || summary.size == 2) {
                val a = summary[0] as? KlassIdentifier
                val b = summary.getOrNull(1) as? KlassIdentifier
                if (a != null) {
                    TreeMapResult.Continue(
                        KlassTypeParameter(a, b)
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
                    keyword = "fun",
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassArgument>(),
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
                    keyword = keyword,
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassArgument>(),
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
                    keyword = "object",
                    identifier = identifier.first(),
                    annotations = summary.filterIsInstance<KlassAnnotation>(),
                    modifiers = summary.filterIsInstance<KlassModifier>(),
                    parameter = summary.filterIsInstance<KlassArgument>(),
                    typeParameters = summary.filterIsInstance<KlassTypeParameter>(),
                    inheritance = summary.filterIsInstance<KlassInheritance>(),
                    type = identifier.getOrNull(1)
                )
            )
        }
    }
