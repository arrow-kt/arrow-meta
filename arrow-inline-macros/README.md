# Module arrow-refined-types

# Package arrow.macros

TODO()

## Setup.

The inline macros plugin is a Kotlin compiler plugin that can be enabled via Gradle in your build by adding it to the
list of your Gradle plugins.

Release

```groovy
plugins {
    id "io.arrow-kt.arrow-inline-macros" version "<release-version>"
}
```

Snapshots

```groovy
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath "io.arrow-kt:arrow-inline-macros-gradle-plugin:<snapshot-version>"
    }
}
apply plugin: "io.arrow-kt.arrow-inline-macros"
```
