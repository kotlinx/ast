package kotlinx.ast.common.klass

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.filter.TreeFilterAll
import kotlinx.ast.common.map.TreeMapBuilder

fun Ast.detachRaw(): AstResult<Unit, List<Ast>> {
    return detachRawMapper(Unit, listOf(this), attachRawAst = false)
}

fun List<Ast>.detachRaw(): AstResult<Unit, List<Ast>> {
    return detachRawMapper(Unit, this, attachRawAst = false)
}

fun Ast.detachRawOrThrow(): Ast {
    val result = detachRaw()
    return when {
        result is AstSuccess && result.success.size == 1 ->
            result.success.first()
        result is AstSuccess ->
            throw RuntimeException("detachRawOrThrow(): expected one ast but found ${result.success.size} results")
        else ->
            throw RuntimeException("detachRawOrThrow(): unexpected result: $result")
    }
}

private val detachRawMapper: TreeMapBuilder<Unit> = TreeMapBuilder<Unit>()
    .convert(
        filter = TreeFilterAll
    ) { klass: Klass ->
        when {
            klass is KlassNode<*> ->
                recursiveChildren(
                    if (klass.raw == null) {
                        klass
                    } else {
                        klass.detachRaw()
                    }
                )
            klass.raw == null ->
                astKeep()
            else ->
                astContinue(klass.detachRaw())
        }
    }.convert(
        filter = TreeFilterAll
    ) { node: AstNode ->
        recursiveChildren(node)
    }
