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

//Plugins

// Proofs
include(":arrow-proofs-plugin")
project(":arrow-proofs-plugin").projectDir = File("plugins/proofs/proofs-plugin")

include(":arrow-proofs-gradle-plugin")
project(":arrow-proofs-gradle-plugin").projectDir = File("plugins/proofs/proofs-gradle-plugin")

include(":arrow-meta-prelude")
project(":arrow-meta-prelude").projectDir = File("plugins/proofs/prelude")

// Optics

include(":arrow-optics-plugin")
project(":arrow-optics-plugin").projectDir = File("plugins/optics/optics-plugin")

include(":arrow-optics-gradle-plugin")
project(":arrow-optics-gradle-plugin").projectDir = File("plugins/optics/optics-gradle-plugin")

// Analysis

include(":arrow-analysis-types")
project(":arrow-analysis-types").projectDir = File("plugins/analysis/types")

include(":arrow-analysis-common")
project(":arrow-analysis-common").projectDir = File("plugins/analysis/common")

include(":arrow-analysis-kotlin-plugin")
project(":arrow-analysis-kotlin-plugin").projectDir = File("plugins/analysis/kotlin-plugin")

include(":arrow-analysis-java-plugin")
project(":arrow-analysis-java-plugin").projectDir = File("plugins/analysis/java-plugin")

include(":arrow-analysis-gradle-plugin")
project(":arrow-analysis-gradle-plugin").projectDir = File("plugins/analysis/gradle-plugin")

include(":arrow-analysis-laws")
project(":arrow-analysis-laws").projectDir = File("plugins/analysis/laws")

include(":arrow-analysis-example")
project(":arrow-analysis-example").projectDir = File("plugins/analysis/example")

include(":arrow-analysis-java-example")
project(":arrow-analysis-java-example").projectDir = File("plugins/analysis/java-example")

//includeBuild("arrow-analysis-sample")
//includeBuild("arrow-proofs-example")
