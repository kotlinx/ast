buildscript {
    dependencies {
        // add the antlr-kotlin-gradle-plugin to the classpath
        classpath("com.strumenta.antlr-kotlin:antlr-kotlin-gradle-plugin:${Versions.antlrKotlin}")
    }
}

plugins {
    base
    kotlin("multiplatform") version Versions.kotlin apply false
}

allprojects {
    group = "kotlinx.ast"
    version = Versions.self

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}

tasks.withType<Wrapper> {
    gradleVersion = Versions.gradle
    distributionType = Wrapper.DistributionType.ALL
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
