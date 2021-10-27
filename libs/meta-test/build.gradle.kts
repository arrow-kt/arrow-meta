plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
  explicitApiWarning()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.classgraph:classgraph:4.8.47")

    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-annotation-processing-embeddable")
    implementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.4") {
        exclude(group = "io.github.classgraph", module = "classgraph")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-annotation-processing-embeddable")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    implementation("org.assertj:assertj-core:3.13.2")
    implementation(projects.arrowMeta)

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
    testRuntimeOnly(projects.arrowMetaPrelude)
}
