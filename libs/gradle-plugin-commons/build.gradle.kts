@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

tasks.processResources {
  filesMatching("**/plugin.properties") {
    filter { it.replace("%compilerPluginVersion%", "$version") }
    filter { it.replace("%kotlinVersion%", libs.versions.kotlin.get()) }
    filter { it.replace("%arrowVersion%", libs.versions.arrow.get()) }
  }
}

kotlin {
  explicitApi = null

  val jvmTargetVersion = properties["jvmTargetVersion"].toString()
  val javaVersion = if (jvmTargetVersion == "1.8") "8" else jvmTargetVersion
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(javaVersion))
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
