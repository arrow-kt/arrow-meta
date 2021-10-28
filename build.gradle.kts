import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.ktlint) apply false
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
    commandLine("sh", "gradlew", "dokkaGfm")
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

    systemProperty(
      "arrow.meta.generate.source.dir",
      File("$buildDir/generated/meta/tests").absolutePath
    )
    systemProperty("CURRENT_VERSION", "$version")
    systemProperty("arrowVersion", libs.versions.arrow.get())
    systemProperty("jvmTargetVersion", properties["jvmTargetVersion"].toString())
    jvmArgs = listOf("""-Dkotlin.compiler.execution.strategy="in-process"""")
  }
}

configure(subprojects - project(":arrow-meta-docs")) {
  apply(plugin = "org.jetbrains.dokka")
  tasks.named<DokkaTask>("dokkaGfm") {
    outputDirectory.set(file("$rootDir/docs/docs/apidocs"))

    dokkaSourceSets {
      val arrowMetaBlobMain = "https://github.com/arrow-kt/arrow-meta/blob/main"

      val kotlinExtension = this@configure.extensions.findByType<KotlinProjectExtension>()
      var taskNumber = 0
      kotlinExtension?.sourceSets?.forEach { sourceSet ->
        sourceSet.kotlin.srcDirs.forEach {
          taskNumber += 1
          named(sourceSet.name + taskNumber) {
            skipDeprecated.set(true)
            reportUndocumented.set(true)
            sourceLink {
              localDirectory.set(it)
              remoteUrl.set(uri("$arrowMetaBlobMain/${relativeProjectPath(it.path)}").toURL())
              remoteLineSuffix.set("#L")
            }
          }
        }
      }

      if (file("src/main/kotlin").exists()) {
        named("main") {
          skipDeprecated.set(true)
          reportUndocumented.set(true)
          sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(
              uri("$arrowMetaBlobMain/${relativeProjectPath("src/main/kotlin")}").toURL()
            )
            remoteLineSuffix.set("#L")
          }
        }
      } else if (file("src/commonMain/kotlin").exists()) {
        named("main") {
          skipDeprecated.set(true)
          reportUndocumented.set(true)
          sourceLink {
            localDirectory.set(file("src/commonMain/kotlin"))
            remoteUrl.set(
              uri("$arrowMetaBlobMain/${relativeProjectPath("src/commonMain/kotlin")}").toURL()
            )
            remoteLineSuffix.set("#L")
          }
        }
      }
    }
  }
}
