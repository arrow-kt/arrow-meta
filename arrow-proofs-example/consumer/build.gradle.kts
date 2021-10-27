plugins {
  kotlin("jvm")
  application
}

application {
  mainClass.set("test2.MainKt")
}

dependencies {
  implementation(project(":"))
}
