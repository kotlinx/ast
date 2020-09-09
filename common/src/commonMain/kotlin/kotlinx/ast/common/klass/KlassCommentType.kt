package kotlinx.ast.common.klass

data class KlassCommentType(
    val rawName: String
) {

    companion object {
        val Doc = KlassCommentType("Doc")
        val SingleLine = KlassCommentType("SingleLine")
        val MultiLine = KlassCommentType("MultiLine")
        val Unknown = KlassCommentType("Unknown")
    }
}
