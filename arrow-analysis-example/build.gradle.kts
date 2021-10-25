plugins {
  kotlin("multiplatform") version "1.5.31"
  id("io.arrow-kt.analysis") version "1.5.31-SNAPSHOT"
}

kotlin {
  jvm()
}

buildscript {
  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
  }

  dependencies {
    classpath("io.arrow-kt:analysis-gradle-plugin:1.5.31-SNAPSHOT")
  }
}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
  }
}
