plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.publishGradlePluginX)
}

version = property("projects.analysis_version").toString()

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
  filesMatching("**/analysis.plugin.properties") {
    filter { it.replace("%analysisPluginVersion%", "$version") }
  }
}

dependencies {
  api(projects.arrowGradlePluginCommons)
  runtimeOnly(libs.classgraph)

  // Necessary during plugin execution to be found and added for compilation
  api(projects.arrowMeta)
  api(projects.arrowAnalysisKotlinPlugin)
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.analysis.kotlin"
      displayName = "Arrow Analysis Kotlin Gradle Plugin"
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
