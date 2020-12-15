package kotlinx.ast.common.ast

data class AstInfoPosition(
    val index: Int,
    val line: Int,
    val row: Int,
) {
    override fun toString(): String {
        return "$line:$row"
    }

    operator fun compareTo(other: AstInfoPosition): Int {
        return when {
            line < other.line ->
                -1
            line > other.line ->
                1
            row < other.row ->
                -1
            row > other.row ->
                1
            else ->
                0
        }
    }

    infix operator fun plus(other: AstInfoPosition): AstInfoPosition {
        return when {
            this == emptyAstInfoPosition ->
                other
            other == emptyAstInfoPosition ->
                this
            this < other ->
                other
            else ->
                this
        }
    }

    infix operator fun minus(other: AstInfoPosition): AstInfoPosition {
        return when {
            this == emptyAstInfoPosition ->
                other
            other == emptyAstInfoPosition ->
                this
            this < other ->
                this
            else ->
                other
        }
    }
}

val emptyAstInfoPosition: AstInfoPosition = AstInfoPosition(-1, -1, -1)

data class AstInfo(
    val id: Int,
    val start: AstInfoPosition,
    val stop: AstInfoPosition,
) {
    override fun toString(): String {
        return (if (this == emptyAstInfo) {
            ""
        } else {
            val id = if (id == -1) {
                ""
            } else {
                id.toString()
            }
            val index = "[${start.index}..${stop.index}]"
            val pos = "[$start..$stop]"
            id.padStart(5) + " " + index.padEnd(12) + " " + pos
        })
    }

    infix operator fun plus(other: AstInfo): AstInfo {
        return emptyAstInfo.copy(
            start = start - other.start,
            stop = stop + other.stop,
        )
    }
}

val AstInfo?.validOrNull: AstInfo?
    get() {
        return when {
            this == null ->
                null
            start > stop ->
                null
            else ->
                this
        }
    }

val emptyAstInfo: AstInfo = AstInfo(
    id = -1,
    start = emptyAstInfoPosition,
    stop = emptyAstInfoPosition,
)
