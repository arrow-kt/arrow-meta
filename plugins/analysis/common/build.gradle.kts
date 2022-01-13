@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

version = property("projects.analysis_version").toString()

kotlin {
  explicitApi = null
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  api(libs.arrowCore)
  api(projects.arrowAnalysisTypes)
  api(libs.javaSmt)
  api(libs.apacheCommonsText)
  api(libs.sarif4k)
}
