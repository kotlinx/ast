package kotlinx.ast.common.ast

interface AstAttachment

val emptyAttachments = AstAttachments()

data class AstAttachments(
    val attachments: Map<AstAttachment, Any> = emptyMap()
) {
    infix operator fun plus(other: AstAttachments): AstAttachments {
        return AstAttachments(
            attachments + other.attachments
        )
    }

    inline operator fun <reified T> get(key: AstAttachment): T? {
        return attachments[key] as T?
    }

    fun attach(key: AstAttachment, value: Any?): AstAttachments {
        return copy(
            attachments = if (value == null) {
                attachments - key
            } else {
                attachments + (key to value)
            }
        )
    }
}

interface AstWithAttachments : Ast {
    val attachments: AstAttachments

    fun withAttachments(attachments: AstAttachments): AstWithAttachments

    fun attach(key: AstAttachment, value: Any?): AstWithAttachments {
        return withAttachments(attachments.attach(key, value))
    }
}

val Ast.astAttachmentsOrNull: AstAttachments?
    get() {
        return if (this is AstWithAttachments) {
            attachments
        } else {
            null
        }
    }

interface AstSelfTypedWithAttachments<Self> : AstSelfTyped<Self>, AstWithAttachments
        where Self : AstSelfTyped<Self>, Self : AstWithAttachments {
    override fun withAttachments(attachments: AstAttachments): Self

    override fun attach(key: AstAttachment, value: Any?): Self {
        return withAttachments(attachments.attach(key, value))
    }
}
