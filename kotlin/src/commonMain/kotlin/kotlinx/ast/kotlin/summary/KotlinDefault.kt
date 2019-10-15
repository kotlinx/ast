package kotlinx.ast.kotlin.summary

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstTerminal
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.kotlin.KotlinAstChannels

val kotlinDefaultMapper: TreeMapMapper = TreeMapMapper()
    .filter { terminal: DefaultAstTerminal ->
        terminal.channel == KotlinAstChannels.default
    }.excludeNames<AstNode>(
        "semi",
        "semis"
    ).excludeNames<AstTerminal>(
        "NL",
        "EOF"
    )
