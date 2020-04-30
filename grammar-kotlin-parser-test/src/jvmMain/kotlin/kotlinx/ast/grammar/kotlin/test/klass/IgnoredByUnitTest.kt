package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.klass.*
import kotlinx.ast.common.map.DefaultTreeMap
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val ignoredByUnitTest: Ast = object : Ast {
    override val description: String = "ignoredByUnitTest"
}

private inline fun <reified K : Klass> TreeMapMapper.ignoredByUnitTest(
    crossinline ignore: K.() -> K
): TreeMapMapper {
    return map { klass: K ->
        if (klass.raw == ignoredByUnitTest) {
            TreeMapResult.Keep
        } else {
            TreeMapResult.Continue(ignore(klass))
        }
    }
}

private fun KlassString.ignoredByUnitTest(): KlassString {
    return copy(raw = ignoredByUnitTest)
}

private fun KlassIdentifier.ignoredByUnitTest(): KlassIdentifier {
    return copy(
        raw = ignoredByUnitTest,
        parameter = parameter.map(KlassIdentifier::ignoredByUnitTest)
    )
}

private fun KlassAnnotation.ignoredByUnitTest(): KlassAnnotation {
    return copy(
        raw = ignoredByUnitTest,
        identifier = identifier.map(KlassIdentifier::ignoredByUnitTest),
        arguments = arguments.map(KlassDeclaration::ignoredByUnitTest)
    )
}

private fun KlassDeclaration.ignoredByUnitTest(): KlassDeclaration {
    return copy(
        raw = ignoredByUnitTest,
        annotations = annotations.map(KlassAnnotation::ignoredByUnitTest),
        expressions = ignoredByUnitTestMapper.treeMap(expressions).get()
    )
}

val ignoredByUnitTestMapper: DefaultTreeMap = DefaultTreeMap(
    TreeMapMapper()
        .ignoredByUnitTest(KlassString::ignoredByUnitTest)
        .ignoredByUnitTest(KlassIdentifier::ignoredByUnitTest)
        .ignoredByUnitTest(KlassAnnotation::ignoredByUnitTest)
        .ignoredByUnitTest(KlassDeclaration::ignoredByUnitTest)
)
