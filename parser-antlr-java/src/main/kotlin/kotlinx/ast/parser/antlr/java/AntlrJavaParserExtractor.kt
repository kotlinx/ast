package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstParserExtractor
import kotlinx.ast.common.AstParserType
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree

interface AntlrJavaParserExtractor<P : Parser, Type : AstParserType> : AstParserExtractor<P, ParseTree, Type>
