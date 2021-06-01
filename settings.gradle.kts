include("common")
include("common-test")
include("parser-antlr-kotlin")
include("parser-antlr-java")
include("parser-antlr-optimized")
include("grammar-antlr4-parser-common")
include("grammar-antlr4-parser-antlr-java")
include("grammar-kotlin-parser-common")
include("grammar-kotlin-parser-test")
include("grammar-kotlin-parser-antlr-kotlin")
include("grammar-kotlin-parser-antlr-java")
include("grammar-kotlin-parser-antlr-optimized")

pluginManagement {
    repositories {
        // used when developing antlr-kotlin gradle plugin
        mavenLocal()
        // used to download antlr-kotlin gradle plugin
        maven("https://jitpack.io")
        // used to download kotlin gradle plugin
        gradlePluginPortal()
    }
}

