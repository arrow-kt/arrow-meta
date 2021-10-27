import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
  explicitApiWarning()
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation(projects.arrowMeta)
  implementation(projects.arrowAnalysisTypes)
  implementation(projects.arrowAnalysisCommon)

  testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
  testImplementation(projects.arrowMetaTest)
  testRuntimeOnly(projects.arrowMeta)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(projects.arrowMetaPrelude)
  testRuntimeOnly(projects.arrowAnalysisKotlinPlugin)
  testRuntimeOnly("io.arrow-kt:arrow-core:1.0.0")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events("passed", "skipped", "failed", "standardOut", "standardError")
  }
  systemProperty("CURRENT_VERSION", properties["VERSION_NAME"].toString())
  systemProperty("ARROW_VERSION", properties["ARROW_VERSION"].toString())
  systemProperty("JVM_TARGET_VERSION", properties["JVM_TARGET_VERSION"].toString())
  jvmArgs = listOf("""-Dkotlin.compiler.execution.strategy="in-process"""")
}
