[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Release](https://jitpack.io/v/kotlinx/ast.svg)](https://jitpack.io/#kotlinx/ast)


# kotlinx.ast

`kotlinx.ast` is a generic AST (Abstract Syntax Tree) parsing library, Kotlin is currently the only supported language.
The library is designed that other languages can be easily added.
`kotlinx.ast` does not use the Kotlin Compiler for parsing,
it is using ANTLR (the Kotlin variant: https://github.com/Strumenta/antlr-kotlin)
using the official Kotlin Grammar (https://kotlinlang.org/docs/reference/grammar.html).

One Component is [Klass](common/src/commonMain/kotlin/kotlinx/ast/common/klass),
a collection of language independent data classes
used to represent and easily access the AST.

## Status
The Project is in an early stage, but it is already possible to parse Kotlin code.
Bug reports, feature requests and pull requests are very welcome.

`kotlinx.ast` is a multiplatform project, but currently, JVM is the only supported target.
Support for JS and native is planned.

`antlr-java`, `antlr-optimized` and `antlr-kotlin` are supported on the JVM.
Multiplatform support (Kotlin Native and Kotlin JavaScript) for `antlr-kotlin` is planned.
Because `antlr-java` and `antlr-optimized` are JVM-only, support for other platforms is not possible.

## Prior art
[kastree](https://github.com/cretz/kastree) is using the kotlin compiler for parsing,
it can only parse kotlin files.
JS and native support is not possible. `kastree` is currently not under active development.

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

There are more examples in directory [testdata](grammar-kotlin-parser-test/src/commonMain/resources/testdata).
* Files named "*.kt.txt" contains the kotlin source to parse
* Files named "*.raw.txt" contains the parsed raw kotlin AST as defined by the official Kotlin Grammar
* Files named "*.summary.txt" contains the AST summary

## Open Tasks

There are some parts missing, for example the _importList_ is not converted into an easy-to-use data class

## Overview

Currently, there are some libraries that are part of kotlinx.ast:
* `kotlinx.ast:common` contains the code that can later be reused by other grammar parsers
* `kotlinx.ast:common-test` contains the dependencies to test frameworks, used by `kotlinx.ast` unit tests
* `kotlinx.ast:parser-antlr-java` contains the shared code required to parse grammars using the official antlr4 JVM implementation.
* `kotlinx.ast:parser-antlr-kotlin` contains the shared code required to parse grammars using antlr-kotlin
* `kotlinx.ast:parser-antlr-optimized` contains the shared code required to parse grammars using the optimized fork of antlr.
* `kotlinx.ast:grammar-kotlin-parser-common` contains the shared code required to parse kotlin source
* `kotlinx.ast:grammar-kotlin-parser-antlr-java` provides a kotlin ast parsed using antlr-java
* `kotlinx.ast:grammar-kotlin-parser-antlr-kotlin` provides a kotlin ast parsed using antlr-kotlin
* `kotlinx.ast:grammar-kotlin-parser-test` contains the test data used by the kotlin parsers
* `kotlinx.ast:grammar-antlr4-parser-common` contains the shared code required to parse antlr4 grammar files
* `kotlinx.ast:grammar-antlr4-parser-antlr-java` provides an antlr4 grammar ast parser using antlr-java
* `kotlinx.ast:grammar-antlr4-parser-test` contains the test data used by the antlr4 grammar parsers

## External Dependencies

[antlr-kotlin](https://github.com/Strumenta/antlr-kotlin)
* `${Versions.antlrKotlinGroup}:antlr-kotlin-runtime:${Versions.antlrKotlin}`
is required at runtime to be able to parse kotlin code into an AST when using `kotlinx.ast:grammar-kotlin-parser-antlr-kotlin`.

[antlr-java](https://github.com/antlr/antlr4)
* `org.antlr:antlr4:${Versions.antlrJava}`
is required at runtime to be able to parse kotlin code into an AST when using `kotlinx.ast:grammar-kotlin-parser-antlr-java`.

[antlr-optimized](https://github.com/tunnelvisionlabs/antlr4)
* `com.tunnelvisionlabs:antlr4:${Versions.antlrOptimized}`
is required at runtime to be able to parse kotlin code into an AST when using `kotlinx.ast:grammar-kotlin-parser-antlr-optimized`.
*NOTE:* `antlr-optimized` is a drop-in replacement of `antlr-java`.
Both jars provide the same API, namely the package `org.antlr.v4`, so you can't use both at the same time.
`kotlinx.ast:parser-antlr-optimized` depends on `kotlinx.ast:parser-antlr-java`, both provide the same `kotlinx.ast` API.
The difference is that `parser-antlr-optimized` will exclude the dependency to `org.antlr:antlr4`
and instead include `com.tunnelvisionlabs:antlr4`.
The generated code of these two libraries is not equal, therefore two different grammar modules are required:
* [kotlinx.ast:parser-antlr-java](grammar-kotlin-parser-antlr-java/src/main/java/kotlinx/ast/grammar/kotlin/target/antlr/java/generated)
* [kotlinx.ast:parser-antlr-optimized](grammar-kotlin-parser-antlr-optimized/src/main/java/kotlinx/ast/grammar/kotlin/target/antlr/optimized/generated)
(the generated code is linked, for the case that you want to compare it)

(`Versions` can be found here: [Versions.kt](buildSrc/src/main/kotlin/Versions.kt))

## How to use kotlinx.ast
There is a small example to get started:
* for `antlr-kotlin` [ExampleMain.kt](grammar-kotlin-parser-antlr-kotlin/src/jvmTest/kotlin/kotlinx/ast/example/ExampleMain.kt)
* for `antlr-java` [ExampleMain.kt](grammar-kotlin-parser-antlr-java/src/test/kotlin/kotlinx/ast/example/ExampleMain.kt)
* for `antlr-optimized` [ExampleMain.kt](grammar-kotlin-parser-antlr-optimized/src/test/kotlin/kotlinx/ast/example/ExampleMain.kt)

## Using with Gradle
`kotlinx.ast` is accessible on Maven & Gradle through Jitpack. In Jitpack basically you can use every commit or tag as a version number. You can find recent versions on the Jitpack page for `kotlinx.ast`.

You have to add this line to `settings.gradle.kts` if use Gradle lower than 5.3: (otherwise, the jars are not resolved):
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
                api("com.github.kotlinx.ast:grammar-kotlin-parser-antlr-kotlin:0123456789")
            }
        }
    }
}
```

If you don't use `kotlin-multiplatform` add this line:
```
// please look at https://jitpack.io/#drieks/antlr-kotlin to find the latest version
implementation("com.github.kotlinx.ast:grammar-kotlin-parser-antlr-kotlin:0123456789")
```
Or, if you prefer to use the `antlr-java` parser (JVM only):
```
// please look at https://jitpack.io/#drieks/antlr-kotlin to find the latest version
implementation("com.github.kotlinx.ast:grammar-kotlin-parser-antlr-java:0123456789")
```
The latest version can be be seen in the Jitpack-Badge at the top of this page.
