package test

@Given object Z {
  val value = "yes!"
}

fun fooZ(@Given x: X, @Config y: Y, @Given z: Z): Triple<String, String, String> =
  Triple(x.value, y.value, z.value)
val resultZ = fooZ()
