package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.map.TreeMapMapper

val expressionsMapper: TreeMapMapper = TreeMapMapper()
    .filterChildren(
        flatten = false,
        childrenNamesToKeep = mapOf(
            "disjunction" to setOf("conjunction"),
            "conjunction" to setOf("equality"),
            "elvisExpression" to setOf("infixFunctionCall"),
            "rangeExpression" to setOf("additiveExpression"),
            "parenthesizedAssignableExpression" to setOf(
                "assignableExpression"
            )
        )
    ).filterChildren(
        flatten = true,
        childrenNamesToKeep = mapOf(
            "parenthesizedDirectlyAssignableExpression" to setOf(
                "directlyAssignableExpression"
            )
        )
    ).flattenNamesNoChildren(
        "disjunction",
        "conjunction",
        "equality",
        "comparison",
        "infixOperation",
        "elvisExpression",
        "infixFunctionCall",
        "rangeExpression",
        "additiveExpression",
        "multiplicativeExpression",
        "asExpression",
        "prefixUnaryExpression",
        "postfixUnaryExpression",
        "primaryExpression"
    )
