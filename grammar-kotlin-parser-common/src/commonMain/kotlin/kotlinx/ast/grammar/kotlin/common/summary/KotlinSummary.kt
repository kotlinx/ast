package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstGroup
import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.klass.identifierName

sealed class KotlinSummary() : AstGroup

data class PackageHeader(
    val identifier: List<KlassIdentifier>
) : KotlinSummary() {
    override val description: String = "PackageHeader(${identifier.identifierName()})"
}

data class Import(
    val identifier: List<KlassIdentifier>,
    val starProjection: Boolean = false,
    val alias: KlassIdentifier? = null
) : KotlinSummary() {
    override val description: String
        get() {
            val extra = when {
                starProjection ->
                    ".*"
                alias != null ->
                    " as ${alias.rawName}"
                else ->
                    ""
            }
            return "Import(${identifier.identifierName()}$extra)"
        }
}
