plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common-test"))
                api(project(":grammar-kotlin-parser-common"))
            }
        }
    }
}
