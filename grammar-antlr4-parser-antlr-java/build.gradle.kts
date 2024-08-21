import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    antlr
    java
    `maven-publish`
}

tasks.withType(Test::class.java).all {
    useJUnitPlatform {}
    maxHeapSize = "4g"
    testLogging {
        showStandardStreams = true
        events = TestLogEvent.values().toSet()
    }
}

dependencies {
    antlr("org.antlr:antlr4:${Versions.antlrJava}")
    api(project(":parser-antlr-java"))
    api(project(":grammar-antlr4-parser-common"))
    testApi(project(":common-test"))
}

sourceSets {
    main {
        java {
            srcDir("src/antlr/java")
        }
    }
}

tasks.generateGrammarSource {
    arguments = arguments + listOf(
        "-no-listener",
        "-no-visitor",
        "-package",
        "org.antlr.parser.antlr4"
    )
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("$projectDir/../grammar-antlr4-parser-common/src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File("$projectDir/src/antlr/java/org/antlr/parser/antlr4")
}

tasks.compileKotlin.configure {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin.configure {
    dependsOn(tasks.generateTestGrammarSource)
}

kotlin {
    jvmToolchain(8)
}
