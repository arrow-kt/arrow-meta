import org.jetbrains.dokka.gradle.DokkaTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.arrowGradleConfig.formatter)
  java
}

allprojects {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  group = property("projects.group").toString()
}

version = property("projects.meta_version").toString()

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

val toolchain = project.extensions.getByType<JavaToolchainService>()
allprojects {
  tasks.withType<JavaCompile>().configureEach {
    javaCompiler.set(toolchain.compilerFor {
      val jvmTargetVersion = properties["jvmTargetVersion"].toString()
      val javaVersion = if (jvmTargetVersion == "1.8") "8" else jvmTargetVersion
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    })
  }
}
