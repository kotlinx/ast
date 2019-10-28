package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast

interface AstParser<Parser, Tree, Type : AstParserType> {
    fun parse(source: AstSource, type: Type): Ast

    fun parse(source: AstSource, types: List<Type>): List<Ast>
}
