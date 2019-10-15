package kotlinx.ast.kotlin.klass

import kotlinx.ast.kotlin.KotlinAstParserType

class SpaceTest : KlassTest(
    " ",
    TestCase(
        parserTypes = listOf(
            KotlinAstParserType.importList
        ),
        print = """
                importList
                  WS >>> <<< (HIDDEN)
                """.trimIndent(),
        summary = listOf(
            node("importList", emptyList())
        )
    )
)
