plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        val commonAntlr by creating {
            dependencies {
                api(kotlin("stdlib-common"))
                api("com.strumenta.antlr-kotlin:antlr-kotlin-runtime:${Versions.antlrKotlin}")
            }
        }

        val commonMain by getting {
            dependsOn(commonAntlr)
            dependencies {
                api(project(":common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
    }
}

tasks.register<com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask>("generateKotlinCommonGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create("org.antlr:antlr4:${Versions.antlr}"),
        project.dependencies.create("com.strumenta.antlr-kotlin:antlr-kotlin-target:${Versions.antlrKotlin}")
    )
    maxHeapSize = "64m"
    packageName = "kotlinx.ast.kotlin.generated"
    arguments = listOf("-no-visitor", "-no-listener")
    source = project.objects
        .sourceDirectorySet("commonAntlr", "commonAntlr")
        .srcDir("src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File("src/commonAntlr/kotlin")
}
