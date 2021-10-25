plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

tasks.processResources {
  filesMatching("**/plugin.properties") {
    filter { it.replace("%COMPILER_PLUGIN_VERSION%", properties["VERSION_NAME"].toString()) }
    filter { it.replace("%KOTLIN_VERSION%", properties["KOTLIN_VERSION"].toString()) }
    filter { it.replace("%ARROW_VERSION%", properties["ARROW_VERSION"].toString()) }
  }
}

dependencies {
  compileOnly(gradleApi())
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
  api("org.jetbrains.kotlin:kotlin-gradle-plugin-api")
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin")
  compileOnly("org.jetbrains.kotlin:kotlin-reflect")
  compileOnly("io.github.classgraph:classgraph:4.8.47")
}
