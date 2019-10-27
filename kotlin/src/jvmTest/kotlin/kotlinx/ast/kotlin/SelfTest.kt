package kotlinx.ast.kotlin

class SelfTest : DirectoryTest(pathOf(".").pathMap().filterNot { (file, _) ->
    if (System.getenv("JITPACK") == "true") {
        // skip this files in jitpack: currently too slow..
        setOf(
            "KotlinLexer.kt",
            "KotlinParser.kt",
            "UnicodeClasses.kt"
        ).contains(file.name)
    } else {
        false
    }
})
