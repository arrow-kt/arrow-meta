plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.publishGradlePluginX)
}

version = property("projects.proofs_version").toString()

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.proofs"
      displayName = "Arrow Proofs Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.ProofsGradlePlugin"
    }
  }
}

dependencies {
  api(projects.arrowGradlePluginCommons)
  runtimeOnly(libs.classgraph)

  // Necessary during plugin execution to be found and added for compilation
  api(projects.arrowMeta)
  api(projects.arrowProofsPlugin)
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}
