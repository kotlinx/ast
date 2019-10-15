package kotlinx.ast.kotlin

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.print

fun main() {
    val source = AstSource.File("kotlin/src/jvmTest/kotlin/kotlinx/ast/kotlin/ExampleMain.kt")
    val kotlinFile = KotlinAstParser.parseKotlinFile(source)
    kotlinFile.summary()
        .onSuccess { astList ->
            astList.forEach(Ast::print)
        }.onFailure { errors ->
            errors.forEach(::println)
        }
}
