package kotlinx.ast.kotlin.klass

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.kotlin.KotlinAstParserType

data class TestCase(
    val parserTypes: List<KotlinAstParserType>,
    val print: String,
    val summary: List<Ast>
) {
    override fun toString(): String {
        return parserTypes.joinToString(",")
    }
}
