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

  val jvmTargetVersion = properties["jvmTargetVersion"].toString()
  val javaVersion = if (jvmTargetVersion == "1.8") "8" else jvmTargetVersion
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(javaVersion))
  }
}

tasks.compileKotlin {
  kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all-compatibility")
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  compileOnly(libs.intellijOpenApi)
  compileOnly(libs.kotlin.reflect)
  api(libs.javaAssist)
  api(libs.kotlin.scriptingCompilerEmbeddable)
  api(libs.kotlin.scriptUtil) {
    exclude(
      group = libs.kotlin.stdlibJDK8.get().module.group,
      module = libs.kotlin.stdlibJDK8.get().module.name
    )
    exclude(
      group = libs.kotlin.compiler.get().module.group,
      module = libs.kotlin.compiler.get().module.name
    )
    exclude(
      group = libs.kotlin.compilerEmbeddable.get().module.group,
      module = libs.kotlin.compilerEmbeddable.get().module.name
    )
  }
  api(libs.kotlin.compilerEmbeddable)

  testCompileOnly(libs.kotlin.compilerEmbeddable)
  testImplementation(libs.junit)
  testImplementation(projects.arrowMetaTest)
  testRuntimeOnly(projects.arrowMeta)
  testRuntimeOnly(libs.kotlin.scriptUtil)
  testRuntimeOnly(libs.kotlin.scriptingCompilerEmbeddable)
  testImplementation(libs.kotlin.stdlibJDK8)
  testRuntimeOnly(libs.arrowCore) {
    exclude(group = libs.kotlin.stdlibJDK8.get().module.group)
  }
  testRuntimeOnly(libs.arrowOptics) {
    exclude(group = libs.kotlin.stdlibJDK8.get().module.group)
  }
}
