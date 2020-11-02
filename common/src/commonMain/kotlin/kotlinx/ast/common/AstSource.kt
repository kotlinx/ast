package kotlinx.ast.common

sealed class AstSource {
    abstract val filename: kotlin.String?
    abstract val description: kotlin.String

    data class File(override val filename: kotlin.String) : AstSource() {
        override val description: kotlin.String = "file:/$filename"
    }

    data class String(
        override val description: kotlin.String,
        val content: kotlin.String
    ) : AstSource() {
        override val filename: kotlin.String? = null
    }
}
