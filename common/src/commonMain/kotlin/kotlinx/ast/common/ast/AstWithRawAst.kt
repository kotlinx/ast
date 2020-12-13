package kotlinx.ast.common.ast

import kotlinx.ast.common.klass.RawAst

object AstAttachmentRawAst : AstAttachment

interface AstWithRawAst : AstWithAttachments {
    val raw: RawAst?
        get() = attachments[AstAttachmentRawAst]

    fun detachRaw(): Ast {
        return withRaw(null)
    }

    fun withRaw(raw: RawAst?): Ast {
        return attach(AstAttachmentRawAst, raw)
    }
}

fun Ast.rawOrNull(): RawAst? {
    return if(this is AstWithRawAst) {
        raw
    } else {
        null
    }
}

interface AstSelfTypedWithRawAst<Self> : AstSelfTypedWithAttachments<Self>, AstWithRawAst
        where Self : AstSelfTypedWithAttachments<Self>, Self : AstWithRawAst {
    override fun detachRaw(): Self {
        return withRaw(null)
    }

    override fun withRaw(raw: RawAst?): Self {
        return attach(AstAttachmentRawAst, raw)
    }
}
