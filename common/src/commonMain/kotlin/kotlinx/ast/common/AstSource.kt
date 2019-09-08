package kotlinx.ast.common

sealed class AstSource {
    data class File(val filename: kotlin.String) : AstSource()

    data class String(val content: kotlin.String) : AstSource()
}
