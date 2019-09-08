include("common")
include("kotlin")

pluginManagement {
    repositories {
        // used when developing antlr-kotlin gradle plugin
        mavenLocal()
        // used to download antlr-kotlin gradle plugin
        gradlePluginPortal()
    }
}

enableFeaturePreview("GRADLE_METADATA")
