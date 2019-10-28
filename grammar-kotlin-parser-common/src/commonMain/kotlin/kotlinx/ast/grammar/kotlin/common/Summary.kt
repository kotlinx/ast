package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.map.DefaultTreeMap
import kotlinx.ast.common.map.TreeMap
import kotlinx.ast.grammar.kotlin.common.summary.*

private val summaryTreeMap: TreeMap = DefaultTreeMap(
    annotationsMapper,
    declarationsMapper,
    expressionsMapper,
    globalsMapper,
    kotlinDefaultMapper,
    modifierMapper,
    stringMapper,
    typesMapper
)

fun List<Ast>.summary(): AstResult<List<Ast>> {
    return summaryTreeMap.treeMap(this)
}

fun Ast.summary(): AstResult<List<Ast>> {
    return listOf(this).summary()
}
