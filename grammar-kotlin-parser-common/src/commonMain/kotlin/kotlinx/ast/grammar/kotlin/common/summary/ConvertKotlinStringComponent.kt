package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.klass.asStringComponent
import kotlinx.ast.common.map.TreeMapContext

fun TreeMapContext<KotlinTreeMapState>.convertKotlinStringComponent(
    stringExpression: AstNode
): AstResult<KotlinTreeMapState, List<Ast>> {
    return recursive(stringExpression.children).flatMap { result ->
        if (result.size == 1) {
            astContinue(
                result.first().asStringComponent()
            )
        } else {
            "found ${stringExpression.description} with ${result.size} expressions!".astError()
        }
    }
}
