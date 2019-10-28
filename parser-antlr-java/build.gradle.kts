plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":common"))
    api("org.antlr:antlr4:${Versions.antlrJava}")
}
