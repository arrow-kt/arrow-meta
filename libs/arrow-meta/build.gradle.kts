plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

version = property("projects.meta_version").toString()

kotlin {
  explicitApi = null
}

tasks.compileKotlin {
  kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses", "-Xjvm-default=enable")
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
