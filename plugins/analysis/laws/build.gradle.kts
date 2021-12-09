@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  alias(libs.plugins.arrowGradleConfig.multiplatform)
  alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

version = property("projects.analysis_version").toString()

kotlin {
  explicitApi = null

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
        api(projects.arrowAnalysisTypes)
      }
    }

    named("jvmMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }

    named("jsMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

dependencies {
  kotlinCompilerClasspath(projects.arrowAnalysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  kotlinOptions {
    dependsOn(":arrow-analysis-kotlin-plugin:jar")
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/plugins/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-$version.jar",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:generatedSrcOutputDir=$buildDir/generated/meta",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:baseDir=${project.rootProject.rootDir.path}"
    )
  }
}

tasks.compileKotlinJs {
  kotlinOptions.suppressWarnings = true
}

tasks.compileKotlinMetadata {
  kotlinOptions.suppressWarnings = true
}
