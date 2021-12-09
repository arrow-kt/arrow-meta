plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

version = property("projects.optics_version").toString()

kotlin {
  explicitApi = null
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.ksp)

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junit)
  testImplementation(projects.arrowMetaTest)
  testRuntimeOnly(projects.arrowMeta)
  testRuntimeOnly(projects.arrowOpticsKsp)
  testRuntimeOnly(libs.arrowAnnotations)
  testRuntimeOnly(libs.arrowCore)
  testRuntimeOnly(libs.arrowOptics)
}
