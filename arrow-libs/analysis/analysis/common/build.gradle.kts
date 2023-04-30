@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

kotlin {
  explicitApi = null
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  api(libs.arrowCore)
  api(projects.arrowAnalysisTypes)
  api(libs.javaSmt)
  api(libs.apacheCommonsText)
  api(libs.sarif4k)
  implementation(libs.kotlinx.serialization.core)
}
