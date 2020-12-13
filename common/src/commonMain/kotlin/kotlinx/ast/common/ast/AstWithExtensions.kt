package kotlinx.ast.common.ast

interface AstWithExtensions : AstWithAttachments, AstWithAstInfo, AstWithRawAst

interface AstSelfTypedWithExtensions<Self> :
    AstSelfTypedWithAttachments<Self>,
    AstSelfTypedWithAstInfo<Self>,
    AstSelfTypedWithRawAst<Self>
        where Self : AstSelfTypedWithAttachments<Self>,
              Self : AstSelfTypedWithAstInfo<Self>,
              Self : AstSelfTypedWithRawAst<Self>
