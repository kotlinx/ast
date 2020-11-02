package kotlinx.ast.common.util

data class AssemblyLine<T>(
    val waiting: List<T> = emptyList(),
    val processed: List<T> = emptyList()
) {
    fun next(): AssemblyLine<T> {
        return if (waiting.isEmpty()) {
            this
        } else {
            copy(
                waiting = waiting.drop(1),
                processed = processed + waiting.first()
            )
        }
    }

    fun isEmpty(): Boolean {
        return waiting.isEmpty()
    }

    fun reset(): AssemblyLine<T> {
        return copy(
            processed = emptyList(),
            waiting = processed + waiting
        )
    }

    fun replace(value: List<T>): AssemblyLine<T> {
        return copy(
            processed = processed + value,
            waiting = waiting.drop(1)
        )
    }
}
