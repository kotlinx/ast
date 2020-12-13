package kotlinx.ast.common.ast

import kotlinx.ast.common.AstChannel

interface AstTerminal : Ast {
    val text: String

    companion object {
        operator fun <T> invoke(
            description: String,
            text: String,
            channel: AstChannel
        ): AstTerminal {
            return DefaultAstTerminal(description, text, channel)
        }
    }
}

data class DefaultAstTerminal(
    override val description: String,
    override val text: String,
    val channel: AstChannel,
    override val attachments: AstAttachments = AstAttachments(),
) : AstTerminal, AstSelfTypedWithAttachments<DefaultAstTerminal> {
    override fun withAttachments(attachments: AstAttachments): DefaultAstTerminal {
        return copy(attachments = attachments)
    }
}
