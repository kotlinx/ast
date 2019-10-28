package kotlinx.ast.parser.antlr.kotlin

import kotlinx.ast.common.AstParserExtractor
import kotlinx.ast.common.AstParserType
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.tree.ParseTree

interface AntlrKotlinParserExtractor<P : Parser, Type : AstParserType> : AstParserExtractor<P, ParseTree, Type>
