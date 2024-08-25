plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))
                api(libs.comStrumenta.antlrKotlinRuntime)
            }
        }
    }
}
