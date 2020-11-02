package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

data class TestCase(
    val name: String,
    val parserTypes: List<KotlinGrammarParserType>,
    val print: String,
    val summary: List<Ast>
) {
    override fun toString(): String {
        return parserTypes.joinToString(",")
    }
}
