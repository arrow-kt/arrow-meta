plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.publishGradlePluginX)
}

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
}

dependencies {
  api(projects.gradlePluginCommons)
  runtimeOnly("io.github.classgraph:classgraph:4.8.47")

  // Necessary during plugin execution to be found and added for compilation
  api(projects.arrowMeta)
  api(projects.analysisKotlinPlugin)
}


gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.analysis"
      displayName = "Arrow Analysis Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.AnalysisGradlePlugin"
    }
  }
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}
