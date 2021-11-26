---
layout: docs
title: Analysis - Java support
---

# Analysis over Java code

Λrrow Analysis has preliminary support for Java. This is still an alpha feature.

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
    classpath("io.arrow-kt.analysis.java:io.arrow-kt.analysis.java.gradle.plugin:2.0-SNAPSHOT")
  }
}

apply(plugin = "io.arrow-kt.analysis.java")
```

</div>

<div id="gradle-groovy" class="tabcontent" markdown="1">

```groovy
buildscript {
  repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
  }
  dependencies {
    classpath 'io.arrow-kt.analysis.java:io.arrow-kt.analysis.java.gradle.plugin:2.0-SNAPSHOT'
  }
}

apply plugin: 'io.arrow-kt.analysis.java'
```

</div>
</div>

## Example

The usage of Λrrow Analysis in Java code is very similar to Kotlin. Note that you **must** import `pre` and `post` functions using `import static` for them to be considered by the plug-in. Following Kotlin's lead, the messages must be represented as lambdas, as we do below.

```java
import static arrow.analysis.RefinementDSLKt.post;
import static arrow.analysis.RefinementDSLKt.pre;

public class Example {
    public int f(int x) {
        pre(x > 0, () -> "x must be positive");
        return post(x + 1, r -> r > 0, () -> "result is positive");
    }
}
```