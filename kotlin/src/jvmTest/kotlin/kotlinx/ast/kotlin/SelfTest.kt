package kotlinx.ast.kotlin

class SelfTest : DirectoryTest(pathOf(".").pathMap().filterNot { (file, _) ->
    // skip this files: currently too slow..
    setOf(
        // unicode? parsing errors
        "StartHere.kt",
        "Framework.kt",

        // too slow...
        "KotlinLexer.kt",
        "KotlinParser.kt",
        "UnicodeClasses.kt"
    ).contains(file.name)
})
