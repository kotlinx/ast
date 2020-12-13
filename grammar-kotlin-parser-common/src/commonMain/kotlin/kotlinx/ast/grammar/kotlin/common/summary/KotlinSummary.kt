package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstAttachments
import kotlinx.ast.common.ast.AstSelfTypedWithExtensions
import kotlinx.ast.common.klass.KlassIdentifier
import kotlinx.ast.common.klass.identifierName

sealed class KotlinSummary() : AstSelfTypedWithExtensions<KotlinSummary>

data class PackageHeader(
    val identifier: List<KlassIdentifier>,
    override val attachments: AstAttachments = AstAttachments(),
) : KotlinSummary() {
    override val description: String = "PackageHeader(${identifier.identifierName()})"

    override fun withAttachments(attachments: AstAttachments): PackageHeader {
        return copy(attachments = attachments)
    }
}

data class Import(
    val identifier: List<KlassIdentifier>,
    val starProjection: Boolean = false,
    val alias: KlassIdentifier? = null,
    override val attachments: AstAttachments = AstAttachments(),
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

    override fun withAttachments(attachments: AstAttachments): Import {
        return copy(attachments = attachments)
    }
}
