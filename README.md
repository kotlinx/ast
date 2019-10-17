[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Release](https://jitpack.io/v/kotlinx/ast.svg)](https://jitpack.io/#kotlinx/ast)


# kotlinx.ast

`kotlinx.ast` is a generic AST (Abstract Syntax Tree) parsing library, Kotlin is currently the only supported language.
The library is designed that other languages can be easily added.
`kotlinx.ast` does not use the Kotlin Compiler for parsing,
it is using ANTLR (the Kotlin variant: https://github.com/Strumenta/antlr-kotlin)
using the official Kotlin Grammer (https://kotlinlang.org/docs/reference/grammar.html).

One Component is [Klass](common/src/commonMain/kotlin/kotlinx/ast/common/klass),
a collection of language independent data classes
used to represent and easily access the AST.

## Status
The Project is in an early stage, but it is already possible to parse Kotlin code.
Bug Reports, Feature Requests and Pull Requests are very welcome.

`kotlinx.ast` is a multiplatform project, but currently, JVM is the only supported target.
Support for JS and native is planned.

## Prior art
[kastree](https://github.com/cretz/kastree) is using the kotlin compiler for parsing,
it can only parse kotlin files.
JS and native support is not possible. kastree is currently not under active development.

## Example

This Kotlin Code:
```
@Annotation1("abc")
// line comment between annotations
@Annotation2("\${123}")
fun parse() {}
```

will be parsed as:
```
PackageHeader()
importList
KlassDeclaration(fun parse)
  KlassAnnotation(Annotation1)
    KlassArgument()
      KlassString
        "abc"
  KlassAnnotation(Annotation2)
    KlassArgument()
      KlassString
        Escape("\$")
        "{123}"
```

There are more examples in directory [testdata](kotlin/src/commonTest/resources/testdata).
* Files named "*.kt.txt" contains the kotlin source to parse
* Files named "*.raw.txt" contains the parsed raw kotlin AST as defined by the official Kotlin Grammer
* Files named "*.summary.txt" contains the AST summary

## Open Tasks

There are some parts missing, for example the _importList_ is not converted into an easy-to-use data class

## Overview

Currently, there are two libraries that are part of kotlinx.ast:
* `kotlinx.ast.common` contains the code that can later be reused by other grammer parsers
* `kotlinx.ast.kotlin` contains the kotlin parsing library

External Dependencies:
* `antlr-kotlin-runtime` is required at runtime to be able to parse kotlin code into an AST.

## How to use kotlinx.ast
There is a small example to get started: [ExampleMain.kt](kotlin/src/jvmTest/kotlin/kotlinx/ast/kotlin/ExampleMain.kt)

## Using with Gradle
`kotlinx.ast` is accessible on Maven & Gradle through Jitpack. In jitpack basically you can use every commit or tag as a version number. You can find recent versions on the Jitpack page for `kotlinx.ast`.

You have to add this line to `settings.gradle.kts`: (otherwise, the jars are not resolved):
```
// Enables KotlinMultiplatform publication and resolving (in dependencies)
enableFeaturePreview("GRADLE_METADATA")
```

You have to add Jitpack to `build.gradle.kts`:
```
repositories {
  maven("https://jitpack.io")
}
```

Add the dependency to `kotlinx.ast` into your project:
```
kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // please look at https://jitpack.io/#drieks/antlr-kotlin to find the latest version
                api("com.github.kotlinx.ast:kotlin:0e92ad018e")
            }
        }
    }
}
```

If you don't use `kotlin-multiplatform` add this line:
```
// please look at https://jitpack.io/#drieks/antlr-kotlin to find the latest version
implementation("com.github.kotlinx.ast:kotlin:0e92ad018e")
```
The latest version can be be seen in the Jitpack-Badge at the top of this page.
