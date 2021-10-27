plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

tasks.processResources {
  filesMatching("**/plugin.properties") {
    filter { it.replace("%COMPILER_PLUGIN_VERSION%", "$version") }
    filter { it.replace("%KOTLIN_VERSION%", libs.versions.kotlin.get()) }
    filter { it.replace("%ARROW_VERSION%", libs.versions.arrow.get()) }
  }
}

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.kotlin.stdlibJDK8)
  api(libs.kotlin.gradlePluginApi)
  compileOnly(libs.kotlin.gradlePluginX)
  compileOnly(libs.kotlin.reflect)
  compileOnly(libs.classgraph)
}
