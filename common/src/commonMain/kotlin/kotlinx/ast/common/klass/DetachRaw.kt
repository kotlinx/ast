package kotlinx.ast.common.klass

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.map.DefaultTreeMap
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

private inline fun <reified K : Klass> TreeMapMapper.detachRaw(
    crossinline ignore: K.() -> K
): TreeMapMapper {
    return map { klass: K ->
        if (klass.raw == null) {
            TreeMapResult.Keep
        } else {
            TreeMapResult.Continue(ignore(klass))
        }
    }
}

private fun KlassString.detachRaw(): KlassString {
    return copy(raw = null)
}

private fun KlassIdentifier.detachRaw(): KlassIdentifier {
    return copy(
        raw = null,
        parameter = parameter.map(KlassIdentifier::detachRaw)
    )
}

private fun KlassAnnotation.detachRaw(): KlassAnnotation {
    return copy(
        raw = null,
        identifier = identifier.map(KlassIdentifier::detachRaw),
        arguments = arguments.map(KlassDeclaration::detachRaw)
    )
}

private fun KlassDeclaration.detachRaw(): KlassDeclaration {
    return copy(
        raw = null,
        annotations = annotations.map(KlassAnnotation::detachRaw),
        expressions = detachRawMapper.treeMap(expressions, attachRawAst = false).get()
    )
}

private val detachRawMapper: DefaultTreeMap = DefaultTreeMap(
    TreeMapMapper()
        .detachRaw(KlassString::detachRaw)
        .detachRaw(KlassIdentifier::detachRaw)
        .detachRaw(KlassAnnotation::detachRaw)
        .detachRaw(KlassDeclaration::detachRaw)
)

fun Ast.detachRaw(): AstResult<List<Ast>> {
    return detachRawMapper.treeMap(this, attachRawAst = false)
}

fun List<Ast>.detachRaw(): AstResult<List<Ast>> {
    return detachRawMapper.treeMap(this, attachRawAst = false)
}

fun Ast.detachRawOrThrow(): Ast {
    val result = detachRaw()
    return when {
        result is AstSuccess && result.result.size == 1 ->
            result.result.first()
        result is AstSuccess ->
            throw RuntimeException("detachRawOrThrow(): expected one ast but found ${result.result.size} results")
        else ->
            throw RuntimeException("detachRawOrThrow(): unexpected result: $result")
    }
}
