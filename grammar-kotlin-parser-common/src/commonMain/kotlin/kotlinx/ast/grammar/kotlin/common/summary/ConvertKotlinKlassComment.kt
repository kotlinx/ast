package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.filter.TreeFilter
import kotlinx.ast.common.klass.KlassComment
import kotlinx.ast.common.klass.KlassCommentType
import kotlinx.ast.common.map.TreeMapBuilder

fun <State> TreeMapBuilder<State>.convertKotlinKlassComment(filter: TreeFilter): TreeMapBuilder<State> {
    return convert(filter = filter) { astComment: AstTerminal ->
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

        astContinueList(
            KlassComment(
                raw = attachRaw(astComment),
                type = type,
                comment = comment
            )
        )
    }
}
