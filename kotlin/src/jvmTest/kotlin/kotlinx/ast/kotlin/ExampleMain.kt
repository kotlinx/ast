package kotlinx.ast.kotlin

import kotlinx.ast.common.AstSource
import kotlinx.ast.common.print

fun main() {
    val source = AstSource.File("kotlin/src/jvmTest/kotlin/kotlinx/ast/kotlin/ExampleMain.kt")
    val ast = KotlinAstParser.parseKotlinFile(source)
    ast.print()
}
