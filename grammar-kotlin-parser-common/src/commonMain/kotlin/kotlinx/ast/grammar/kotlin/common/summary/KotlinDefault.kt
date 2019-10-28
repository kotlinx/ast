package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstTerminal
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarAstChannels

val kotlinDefaultMapper: TreeMapMapper = TreeMapMapper()
    .filter { terminal: DefaultAstTerminal ->
        terminal.channel == KotlinGrammarAstChannels.default
    }.excludeNames<AstNode>(
        "semi",
        "semis"
    ).excludeNames<AstTerminal>(
        "NL",
        "EOF"
    )
