package kotlinx.ast.grammar.kotlin.target.antlr.java

import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.print
import kotlinx.ast.grammar.kotlin.common.summary

fun main() {
    val source = AstSource.File("kotlin/src/jvmTest/kotlin/kotlinx/ast/kotlin/ExampleMain.kt")
    val kotlinFile = KotlinGrammarAntlrJavaParser.parseKotlinFile(source)
    kotlinFile.summary()
        .onSuccess { astList ->
            astList.forEach(Ast::print)
        }.onFailure { errors ->
            errors.forEach(::println)
        }
}
