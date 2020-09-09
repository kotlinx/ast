package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.klass.KlassComment
import kotlinx.ast.common.klass.KlassCommentType
import kotlinx.ast.common.map.TreeMapMapper
import kotlinx.ast.common.map.TreeMapResult

val commentsMapper: TreeMapMapper = TreeMapMapper()
    .map(setOf("DelimitedComment", "LineComment")) { astComment: AstTerminal ->
        val text = astComment.text
        val (type, comment) = when {
            text.startsWith("/**") ->
                Pair(KlassCommentType.Doc, text.drop(3).dropLast(2))
            text.startsWith("/*") ->
                Pair(KlassCommentType.MultiLine, text.drop(2).dropLast(2))
            text.startsWith("//") ->
                Pair(KlassCommentType.SingleLine, text.drop(2))
            else ->
                Pair(KlassCommentType.Unknown, text)
        }

        TreeMapResult.Continue(
            KlassComment(
                raw = attach(astComment),
                type = type,
                comment = comment
            )
        )
    }
