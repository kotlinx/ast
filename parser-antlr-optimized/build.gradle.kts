plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":parser-antlr-java")) {
        exclude("org.antlr", "antlr4")
    }
    api(libs.comTunnelvisionlabs.antlr4)
}

kotlin {
    jvmToolchain(8)
}
