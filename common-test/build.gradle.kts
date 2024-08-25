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
                implementation(libs.chQosLogback.logbackClassic)
                api(libs.ioKotest.kotestRunnerJunit5)
            }
        }
    }
}
