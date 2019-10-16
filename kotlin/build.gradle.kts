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
                api(kotlin("stdlib-common"))
                api("${Versions.antlrKotlinGroup}:antlr-kotlin-runtime:${Versions.antlrKotlin}")
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

        val jvmTest by getting {
            dependencies {
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("io.kotlintest:kotlintest-runner-junit5:3.4.0")
            }
        }
    }
}

tasks.register<com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask>("generateKotlinCommonGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create("org.antlr:antlr4:${Versions.antlr}"),
        project.dependencies.create("${Versions.antlrKotlinGroup}:antlr-kotlin-target:${Versions.antlrKotlin}")
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

tasks.withType(Test::class.java).all {
    useJUnitPlatform {}
    testLogging {
        showStandardStreams = true
        events = TestLogEvent.values().toSet()
    }
}
