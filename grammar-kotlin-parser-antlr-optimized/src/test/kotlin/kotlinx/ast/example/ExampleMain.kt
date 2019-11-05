package kotlinx.ast.example

import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.print
import kotlinx.ast.grammar.kotlin.common.summary
import kotlinx.ast.grammar.kotlin.target.antlr.optimized.KotlinGrammarAntlrOptimizedParser

fun main() {
    val source = AstSource.File(
        "grammar-kotlin-parser-antlr-java/src/test/kotlin/kotlinx/ast/example/ExampleMain.kt"
    )
    val kotlinFile = KotlinGrammarAntlrOptimizedParser.parseKotlinFile(source)
    kotlinFile.summary()
        .onSuccess { astList ->
            astList.forEach(Ast::print)
        }.onFailure { errors ->
            errors.forEach(::println)
        }
}
