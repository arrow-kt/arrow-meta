plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
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
  api(projects.arrowAnalysisJavaPlugin)
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.analysis.java"
      displayName = "Arrow Analysis Java Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.AnalysisJavaGradlePlugin"
    }
  }
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}
