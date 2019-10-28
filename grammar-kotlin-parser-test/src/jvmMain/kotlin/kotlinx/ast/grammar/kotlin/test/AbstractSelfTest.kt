package kotlinx.ast.grammar.kotlin.test

import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser

abstract class AbstractSelfTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractDirectoryTest<Parser>(parser, pathOf(".").pathMap().filterNot { (file, _) ->
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
