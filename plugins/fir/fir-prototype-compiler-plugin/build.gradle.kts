plugins {
    alias(libs.plugins.arrowGradleConfig.jvm)
    alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
    explicitApiWarning()
}

dependencies {
    compileOnly(libs.kotlin.stdlibJDK8)
    implementation(projects.arrowMeta)

    testImplementation(libs.kotlin.stdlibJDK8)
    testImplementation(libs.junit)
    testImplementation(projects.arrowMetaTest)
}
