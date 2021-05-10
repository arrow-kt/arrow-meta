---
layout: docs
title: Setup
---

# Setup

## Λrrow Compiler Plugins

The compiler plugins can be enabled in your project with the Arrow Gradle Plugin.

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

For instance, to use Arrow Proofs Compiler Plugin:

```
arrowMeta.plugins = ['proofs']
```

To use Arrow Optics Plugin:

```
arrowMeta.plugins = ['optics']
```

To use Arrow Proofs Compiler Plugin and then a custom compiler plugin based on Arrow Meta:

```
arrowMeta.plugins = ['proofs', '(...)/custom-plugin.jar']
```

In case the custom compiler plugin is published in an artifacts repository:

```
buildscript {
    ...
    dependencies {
        ...
        classpath "<groupId>:<artifactId>:<version>"
    }
}

...

arrowMeta.plugins = ['proofs', '<artifactId>:<version>']
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
