package kotlinx.ast.common.ast

interface AstWithExtensions : AstWithAttachments, AstWithRawAst

interface AstSelfTypedWithExtensions<Self> :
    AstSelfTypedWithAttachments<Self>,
    AstSelfTypedWithRawAst<Self>
        where Self : AstSelfTypedWithAttachments<Self>,
              Self : AstSelfTypedWithRawAst<Self>
