import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val commonAntlr by creating {
            dependencies {
                api(project(":parser-antlr-kotlin"))
            }
        }

        val commonMain by getting {
            dependsOn(commonAntlr)
            dependencies {
                api(project(":grammar-kotlin-parser-common"))
            }
        }

        val jvmTest by getting {
            dependencies {
                api(project(":grammar-kotlin-parser-test"))
            }
        }
    }
}

tasks.register<com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask>("generateKotlinCommonGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create("org.antlr:antlr4:${Versions.antlrUsedByAntlrKotlin}"),
        project.dependencies.create("${Versions.antlrKotlinGroup}:antlr-kotlin-target:${Versions.antlrKotlin}")
    )
    maxHeapSize = "64m"
    packageName = "kotlinx.ast.grammar.kotlin.target.antlr.kotlin.generated"
    arguments = listOf("-no-visitor", "-no-listener")
    source = project.objects
        .sourceDirectorySet("commonAntlr", "commonAntlr")
        .srcDir("../grammar-kotlin-parser-common/src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File("src/commonAntlr/kotlin")
}

tasks.withType(Test::class.java).all {
    useJUnitPlatform {}
    maxHeapSize = "4g"
    testLogging {
        showStandardStreams = true
        events = TestLogEvent.values().toSet()
    }
}
