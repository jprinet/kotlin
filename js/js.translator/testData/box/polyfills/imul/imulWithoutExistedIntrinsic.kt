// IGNORE_BACKEND: JS
// FILE: main.js
var isLegacyBackend =
    typeof Kotlin != "undefined" && typeof Kotlin.kotlin != "undefined"

if (!isLegacyBackend) {
    Math.imul = undefined
}

// FILE: main.kt
fun box(): String {
    val a: Int = 2
    val b: Int = 42
    val c: Int = 44
    val d: Int = -2

    assertEquals(a * b, 84)
    assertEquals(a * c, 88)
    assertEquals(a * d, -4)
    assertEquals(js("Math.imul.called"), js("undefined"))

    return "OK"
}
