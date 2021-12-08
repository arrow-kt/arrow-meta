plugins {
  alias(libs.plugins.kotlin.jvm)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

version = property("projects.optics_version").toString()

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
  filesMatching("**/optics.plugin.properties") {
    filter { it.replace("%opticsPluginVersion%", "$version") }
    filter { it.replace("%arrowVersion%", libs.versions.arrow.get()) }
  }
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.optics"
      displayName = "Arrow Optics Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.OpticsGradlePlugin"
    }
  }
}

dependencies {
  api(projects.arrowGradlePluginCommons)
  runtimeOnly(libs.classgraph)

  // Necessary during plugin execution to be found and added for compilation
  api(projects.arrowMeta)
  api(projects.arrowOpticsPlugin)
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}
