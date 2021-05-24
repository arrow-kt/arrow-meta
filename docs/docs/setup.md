---
layout: docs
title: Setup
---

# Setup

## Λrrow Compiler Plugins

The compiler plugins can be enabled in your project with the correspondent Gradle Plugin.

### Refined Types Compiler Plugin

```
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath "io.arrow-kt:arrow-proofs-gradle-plugin:<snapshot-version>"
    }
}

apply plugin: "io.arrow-kt.proofs"
```

### Optics Compiler Plugin

```
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath "io.arrow-kt:arrow-optics-gradle-plugin:<snapshot-version>"
    }
}

apply plugin: "io.arrow-kt.optics"
```

### Proofs Compiler Plugin

```
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath "io.arrow-kt:arrow-proofs-gradle-plugin:<snapshot-version>"
    }
}

apply plugin: "io.arrow-kt.proofs"
```

# See Also

Take a look at [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository.

# Future

Gradle Plugins will be published in the [Gradle Plugins Portal](https://plugins.gradle.org) for RELEASE versions and they will be able to be used with the plugins DSL:

```
plugins {
    id "<plugin-id>" version "<release-version>"
}
```
