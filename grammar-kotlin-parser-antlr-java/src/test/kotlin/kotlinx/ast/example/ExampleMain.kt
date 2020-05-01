package kotlinx.ast.example

import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.print
import kotlinx.ast.grammar.kotlin.common.summary
import kotlinx.ast.grammar.kotlin.target.antlr.java.KotlinGrammarAntlrJavaParser

fun main() {
    val source = AstSource.File(
        "grammar-kotlin-parser-antlr-java/src/test/kotlin/kotlinx/ast/example/ExampleMain.kt"
    )
    val kotlinFile = KotlinGrammarAntlrJavaParser.parseKotlinFile(source)
    kotlinFile.summary(attachRawAst = false)
        .onSuccess { astList ->
            astList.forEach(Ast::print)
        }.onFailure { errors ->
            errors.forEach(::println)
        }
}
