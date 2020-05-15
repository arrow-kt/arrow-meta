---
layout: docs
title: Setup
---

# Setup

## Λrrow Meta Compiler Plugin

The Arrow Meta Compiler Plugin can be enabled in your project with the Arrow Meta Gradle Plugin.

The Arrow Meta Gradle Plugin will be published in the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.arrow-kt.arrow) for RELEASE versions and it will be able to be used with the plugins DSL:

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

## Λrrow Meta IDE Plugin

Arrow Meta IDE Plugin is compatible with the same versions as [Kotlin Plugin for Intellij IDEA](https://plugins.jetbrains.com/plugin/6954-kotlin).

### Λrrow Meta IDE Plugin: installation from a Gradle task

When using the Arrow Meta Gradle Plugin from Intellij IDEA, there is a Gradle task available to install the Arrow Meta IDE Plugin.

This message will appear with every Gradle task execution if Arrow Meta IDE Plugin is not installed, for instance, when running a `clean` task:

```
> Configure project :
Arrow Meta IDE Plugin is not installed!
Run 'install-idea-plugin' Gradle task under 'Arrow Meta' group to install it.
```

Go to `Arrow Meta` group for Gradle tasks and run `install-idea-plugin`:

```
> Task :install-idea-plugin
Arrow Meta IDE Plugin is not installed! Downloading ...
Restart Intellij IDEA to finish the installation!
```

It will be necessary to restart the IDE to finish the installation.

### Λrrow Meta IDE Plugin: installation from a custom plugin repository

There are two custom plugin repositories according to the correspondent version:

* For the latest SNAPSHOT version: `https://meta.arrow-kt.io/idea-plugin/latest-snapshot/updatePlugins.xml`
* For the latest RELEASE version: `https://meta.arrow-kt.io/idea-plugin/latest-release/updatePlugins.xml`

Follow the steps about the use of [Custom plugin repositories](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_repo) to install the Arrow Meta IDE Plugin.

### Λrrow Meta IDE Plugin: installation from Jetbrains Plugins Repository

When a RELEASE version is published into the [Jetbrains Plugins Repository](https://plugins.jetbrains.com), it will be possible [to install the plugin from the Marketplace](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_repo).

See more details at [Arrow Meta for Intellij IDEA](https://plugins.jetbrains.com/plugin/14291-arrow-meta).

### Λrrow Meta IDE Plugin: how to extend it or to develop from it

If you are interested in extending the Arrow Meta IDE Plugin or developing from it:

```
dependencies {
    implementation "io.arrow-kt:idea-plugin:<version>"
}

repositories {
    maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' }
}
```

# See Also

Take a look at [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository.
