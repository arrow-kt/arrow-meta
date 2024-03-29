import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.arrowGradleConfig.versioning)
  java
}

allprojects {
  group = property("projects.group").toString()
}

tasks {
  create<Exec>("generateDoc") {
    commandLine("sh", "gradlew", "dokkaJekyll")
  }

  create("buildMetaDoc") {
    group = "documentation"
    description = "Generates API Doc and validates all the documentation"
    dependsOn("generateDoc")
  }
}

// declare Dokka implicit dependencies
val libNames = listOf("arrow-meta", "arrow-meta-test", "arrow-gradle-plugin-commons")
task("docsJar") {
  libNames.forEach {
    dependsOn(tasks.getByPath(":${it}:dokkaHtml"))
  }
}
libNames.forEach { task ->
  libNames.forEach {
    tasks.getByPath(":${task}:docsJar").dependsOn(":${it}:dokkaHtml")
  }
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

allprojects {
  extra.set("dokka.outputDirectory", rootDir.resolve("docs/docs/apidocs"))
}

configure(subprojects - project(":arrow-meta-docs")) {
  apply(plugin = "org.jetbrains.dokka")
  tasks.named<DokkaTask>("dokkaJekyll") {
    outputDirectory.set(file("$rootDir/docs/docs/apidocs"))

    dokkaSourceSets {
      val arrowMetaBlobMain = "https://github.com/arrow-kt/arrow-meta/blob/main"

      configureEach {
        skipDeprecated.set(true)
        reportUndocumented.set(true)
        sourceRoots.filter { it.path.contains(file("test/").path, ignoreCase = true) }
          .forEach {
            val file = it.relativeTo(projectDir)
            println("HELLO: $file")
            println("HELLO2: ${uri("$arrowMetaBlobMain/$file").toURL()}")
            sourceLink {
              localDirectory.set(file)
              remoteUrl.set(
                uri("$arrowMetaBlobMain/$file").toURL()
              )
              remoteLineSuffix.set("#L")
            }
          }
      }
    }
  }
}
