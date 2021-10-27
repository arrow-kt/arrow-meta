plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
  id("org.jetbrains.dokka") version "1.5.30" apply false
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply false
}

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  group = property("projects.group").toString()
  version = property("projects.version").toString()
}

tasks {
  create<Exec>("generateDoc") {
    commandLine("sh", "gradlew", "dokka")
  }

  create<Exec>("runValidation") {
    commandLine("sh", "gradlew", ":docs:runAnk")
  }

  create("buildMetaDoc") {
    group = "documentation"
    description = "Generates API Doc and validates all the documentation"
    dependsOn("generateDoc")
    dependsOn("runValidation")
  }

  named("runValidation").get().mustRunAfter("generateDoc")
}

allprojects {
  this.tasks.withType<Test>() {
    useJUnitPlatform()
    testLogging {
      showStandardStreams = true
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      events("passed", "skipped", "failed", "standardOut", "standardError")
    }

    systemProperty("arrow.meta.generate.source.dir", File("$buildDir/generated/meta/tests").absolutePath)
    systemProperty("CURRENT_VERSION", version)
    systemProperty("ARROW_VERSION", libs.versions.arrow.get())
    systemProperty("JVM_TARGET_VERSION", properties["JVM_TARGET_VERSION"].toString())
    jvmArgs = listOf("""-Dkotlin.compiler.execution.strategy="in-process"""")
  }
}
