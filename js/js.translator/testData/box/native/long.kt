// EXPECTED_REACHABLE_NODES: 1273
// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: UNSUPPORTED_JS_INTEROP
// KJS_WITH_FULL_RUNTIME

// Test that APIs expecting Number behave correctly with Long values.

import kotlin.js.Date

fun box(): String {
    assertEquals("1970-01-01T00:00:00.000Z", Date(0L).toISOString())
    assertEquals("04/10/1995, 00:00:00", Date(1995, 9, 4, 0, 0, 0, 0L).toLocaleString("en-GB"))
    assertEquals(812764800000.0, Date.UTC(1995, 9, 4, 0, 0, 0, 0L))

    return "OK"
}
