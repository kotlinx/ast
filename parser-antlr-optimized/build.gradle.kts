plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":parser-antlr-java")) {
        exclude("org.antlr", "antlr4")
    }
    api("com.tunnelvisionlabs:antlr4:${Versions.antlrOptimized}")
}

kotlin {
    jvmToolchain(8)
}
