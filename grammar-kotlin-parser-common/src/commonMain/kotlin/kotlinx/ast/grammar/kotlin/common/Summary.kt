package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.grammar.kotlin.common.summary.KotlinTreeMapState
import kotlinx.ast.grammar.kotlin.common.summary.kotlinTreeMapBuilder

fun Ast.summary(attachRawAst: Boolean): AstResult<Unit, List<Ast>> {
    return listOf(this).summary(attachRawAst = attachRawAst)
}

fun List<Ast>.summary(attachRawAst: Boolean): AstResult<Unit, List<Ast>> {
    return kotlinTreeMapBuilder(
        state = KotlinTreeMapState(),
        ast = this,
        attachRawAst = attachRawAst
    ).flatMap { ast ->
        Unit.astSuccess(ast)
    }
}
