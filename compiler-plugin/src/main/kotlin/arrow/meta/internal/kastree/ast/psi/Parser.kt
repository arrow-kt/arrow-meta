package arrow.meta.internal.kastree.ast.psi

open class Parser(val converter: Converter = Converter) {
  companion object : Parser() {
    init {
      // To hide annoying warning on Windows
      System.setProperty("idea.use.native.fs.for.win", "false")
    }
  }
}
