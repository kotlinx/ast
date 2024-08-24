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
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("ch.qos.logback:logback-classic:1.2.10")
                api("io.kotest:kotest-runner-junit5:${Versions.kotest}")
            }
        }
    }
}
