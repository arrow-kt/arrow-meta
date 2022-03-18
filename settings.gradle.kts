enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("gradle/projects.libs.versions.toml"))
      val kotlinVersion: String? by settings
      kotlinVersion?.let { version("kotlin", it) }
    }
  }
}

rootProject.name = "arrow-meta-workspace"

// Libraries

include(":arrow-meta")
project(":arrow-meta").projectDir = File("libs/arrow-meta")

include(":arrow-meta-test")
project(":arrow-meta-test").projectDir = File("libs/meta-test")

include(":arrow-gradle-plugin-commons")
project(":arrow-gradle-plugin-commons").projectDir = File("libs/gradle-plugin-commons")

// Docs

include(":arrow-meta-docs")
project(":arrow-meta-docs").projectDir = File("docs")

