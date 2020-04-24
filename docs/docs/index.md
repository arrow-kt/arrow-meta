---
layout: docs
title: Quick Start
permalink: /
video: WKR384ZeBgk
---

[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=%230576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.jfrog.org%2Fartifactory%2Foss-snapshot-local%2Fio%2Farrow-kt%2Fcompiler-plugin%2Fmaven-metadata.xml)](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/compiler-plugin/)
[![Publish artifacts](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Artifacts/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Artifacts%22)
[![Publish documentation](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Documentation/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Documentation%22)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Functional companion to Kotlin's Compiler & IDE

Λrrow Meta is a meta-programming library that cooperates with the Kotlin compiler in all its phases, bringing its full power to the community.

Writing compiler plugins, source transformations, IDEA plugins, linters, type search engines, and automatic code refactoring are just a few of the things that can be accomplished with Meta.

{:.gif}
![Hello World Compiler Plugin Demo]({{ 'img/demos/hello-world-compiler-plugin.gif' | relative_url }})

## Λrrow Meta Compiler Plugin

The Arrow Meta Compiler Plugin can be enabled in your project with the Arrow Meta Gradle Plugin.

It will be published in the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.arrow-kt.arrow) for RELEASE versions and it will be able to be used with the plugins DSL:

```
plugins {
  id "io.arrow-kt.arrow" version "<release-version>"
}
```

If using a SNAPSHOT version, it must be included with the legacy plugin application:

```
buildscript {
  repositories {
    maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local/" }
  }
  dependencies {
    classpath "io.arrow-kt:gradle-plugin:<snapshot-version>"
  }
}

apply plugin: "io.arrow-kt.arrow"
```

## Λrrow Meta Intellij IDEA Plugin

There are several ways to install the Arrow Meta Intellij IDEA Plugin.

### Λrrow Meta Intellij IDEA Plugin: installation from a Gradle task

When using the Arrow Meta Gradle Plugin from Intellij IDEA, there is a Gradle task available to install it:

(screenshots)

### Λrrow Meta Intellij IDEA Plugin: installation from a private repository

Besides the [Jetbrains Plugins Repository](https://plugins.jetbrains.com), there are 2 private repositories:

* To install the latest SNAPSHOT version: https://meta.arrow-kt.io/idea-plugin/latest-snapshot/updatePlugins.xml
* To install the latest RELEASE version: https://meta.arrow-kt.io/idea-plugin/latest-release/updatePlugins.xml

(screenshots)

## Λrrow Meta Intellij IDEA Plugin: installation from Jetbrains Plugins Repository

When a RELEASE version is published into the [Jetbrains Plugins Repository](https://plugins.jetbrains.com), it will be able to install the plugin from ...

(screenshots)

## Λrrow Meta examples

Take a look at [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository for more details.
