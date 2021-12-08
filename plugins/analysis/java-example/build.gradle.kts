plugins {
  `java-library`
}

version = property("projects.analysis_version").toString()

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {
  implementation(libs.kotlin.stdlibCommon)
  implementation(libs.kotlin.stdlibJDK8)
  implementation(projects.arrowAnalysisTypes)
  // compileOnly(projects.arrowAnalysisLaws)
  compileOnly(projects.arrowAnalysisJavaPlugin)
  compileOnly(projects.arrowAnalysisCommon)
  annotationProcessor(projects.arrowAnalysisJavaPlugin)
}

tasks.withType<JavaCompile> {
  javaCompiler.set(javaToolchains.compilerFor {
    languageVersion.set(JavaLanguageVersion.of(11))
  })
  options.compilerArgs.addAll(listOf(
    "-parameters",
    "-Xplugin:ArrowAnalysisJavaPlugin baseDir=${project.rootProject.rootDir.path}"
  ))
}
