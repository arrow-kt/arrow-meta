---
layout: docs
title: Setup
---

# Setup

## Λrrow Meta Compiler Plugin

The Arrow Meta Compiler Plugin can be enabled in your project with the Arrow Meta Gradle Plugin.

If using a SNAPSHOT version, it must be included with the legacy plugin application:

```
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath "io.arrow-kt:gradle-plugin:<snapshot-version>"
    }
}

apply plugin: "io.arrow-kt.arrow"
```

Then add the list of the required compiler plugins on `arrowMeta.plugins`.

For instance, for using Arrow Meta Proofs Compiler Plugin:

```
arrowMeta.plugins = ['proofs']
```

For using Arrow Meta Proofs Optics Plugin:

```
arrowMeta.plugins = ['optics']
```

For using Arrow Meta Proofs Compiler Plugin and then a custom compiler plugin based on Arrow Meta:

```
arrowMeta.plugins = ['proofs', '(...)/custom-plugin.jar']
```

# See Also

Take a look at [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository.

# Future

The Arrow Meta Gradle Plugin will be published in the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.arrow-kt.arrow) for RELEASE versions and it will be able to be used with the plugins DSL:

```
plugins {
    id "io.arrow-kt.arrow" version "<release-version>"
}
```
