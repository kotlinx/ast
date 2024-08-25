plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":common"))
    api(libs.orgAntlr.antlr4)
}

kotlin {
    jvmToolchain(8)
}
