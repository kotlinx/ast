package kotlinx.ast.kotlin

class SelfTest : DirectoryTest(pathOf(".").pathMap().filterNot { (file, _) ->
    // skip this files: currently too slow..
    setOf(
        "KotlinLexer.kt",
        "KotlinParser.kt",
        "UnicodeClasses.kt"
    ).contains(file.name)
})
