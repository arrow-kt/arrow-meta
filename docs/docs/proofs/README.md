---
layout: docs
title: Proofs - Quick Start
permalink: /proofs/
---

# Proofs Quick Start

**🛑 The Proofs plug-in is no longer in development. For information about similar projects, please check our channel in the [Kotlin Slack](https://kotlinlang.slack.com/).**

## Adding the plug-in

Open your Gradle build file, and add the following lines:

<div class="setup-gradle" markdown="1">
<!-- Tab links -->
<div class="tab" markdown="1">
  <button class="tablinks" onclick="openSetup(event, 'gradle-kotlin')" id="defaultOpen" markdown="1">Gradle Kotlin DSL</button>
  <button class="tablinks" onclick="openSetup(event, 'gradle-groovy')" markdown="1">Gradle Groovy DSL</button>
</div>

<div id="gradle-kotlin" class="tabcontent" markdown="1">

```kotlin
buildscript {
  repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }
  dependencies {
    classpath("io.arrow-kt.proofs:io.arrow-kt.proofs.gradle.plugin:2.0-SNAPSHOT")
  }
}

apply(plugin = "io.arrow-kt.proofs")
```

</div>

<div id="gradle-groovy" class="tabcontent" markdown="1">

```groovy
buildscript {
    repositories {
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
    dependencies {
        classpath 'io.arrow-kt.proofs:io.arrow-kt.proofs.gradle.plugin:2.0-SNAPSHOT'
    }
}

apply plugin: 'io.arrow-kt.proofs'
```

</div>
</div>

## Using proofs

Using `@Given` and `@Config` you can make your code wonderful.

## Going further

Proofs is so cool! Go around and learn more.