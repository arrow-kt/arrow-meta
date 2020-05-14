---
layout: docs
title: Setup
---

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
