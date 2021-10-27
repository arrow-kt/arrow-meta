buildscript {
    extra.set("PATH_APIDOCS", "$rootDir/docs/docs/apidocs")
}

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
  id("org.jetbrains.dokka") version "1.5.30" apply false
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply false
}

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }
}

tasks {
  create<Exec>("generateDoc") {
    commandLine("sh", "gradlew", "dokka")
  }

  create<Exec>("runValidation") {
    commandLine("sh", "gradlew", ":docs:runAnk")
  }

  create("buildMetaDoc") {
    group = "documentation"
    description = "Generates API Doc and validates all the documentation"
    dependsOn("generateDoc")
    dependsOn("runValidation")
  }

  named("runValidation").get().mustRunAfter("generateDoc")
}
