plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api("com.strumenta.antlr-kotlin:antlr-kotlin-runtime:${Versions.antlrKotlin}")
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
    }
}
