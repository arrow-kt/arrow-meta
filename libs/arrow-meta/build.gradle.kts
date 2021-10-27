plugins {
    alias(libs.plugins.arrowGradleConfig.jvm)
    alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
    explicitApiWarning()
}

tasks.compileKotlin {
  kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses", "-Xjvm-default=enable")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("com.intellij:openapi:7.0.3")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
    api("org.javassist:javassist:3.27.0-GA")
    api("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable")
    api("org.jetbrains.kotlin:kotlin-script-util") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    testCompileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
    testImplementation(projects.arrowMetaTest)
    testRuntimeOnly(projects.arrowMeta)
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-script-util")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib")
    testRuntimeOnly("io.arrow-kt:arrow-core:1.0.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("io.arrow-kt:arrow-optics:1.0.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly(projects.arrowMetaPrelude)
}
