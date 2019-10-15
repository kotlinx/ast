package kotlinx.ast.kotlin

import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.AstParser
import kotlinx.ast.common.AstSource
import kotlinx.ast.kotlin.generated.KotlinLexer
import kotlinx.ast.kotlin.generated.KotlinParser

object KotlinAstParser : AstParser<KotlinParser>(::KotlinLexer, ::KotlinParser) {
    fun parseKotlinFile(source: AstSource): Ast {
        return parse(source, KotlinAstParserType.kotlinFile)
    }
}
