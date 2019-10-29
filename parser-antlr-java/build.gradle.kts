plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":common"))
    implementation("org.antlr:antlr4:${Versions.antlrJava}")
}
