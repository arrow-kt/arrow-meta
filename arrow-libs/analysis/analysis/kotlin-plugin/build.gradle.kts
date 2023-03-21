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
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  implementation(libs.arrowMeta)
  implementation(projects.arrowAnalysisTypes)
  implementation(projects.arrowAnalysisCommon)

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junit)
  testImplementation(libs.junitEngine)
  testImplementation(libs.junitPlatformLauncher)
  testImplementation(libs.arrowMetaTest)
  testRuntimeOnly(libs.arrowMeta)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(projects.arrowAnalysisKotlinPlugin)
  testRuntimeOnly(libs.arrowCore)

  testImplementation(libs.ksp.api)
  testImplementation(libs.ksp.lib)
}
