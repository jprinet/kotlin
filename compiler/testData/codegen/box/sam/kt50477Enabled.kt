// !LANGUAGE: +SuspendOnlySamConversions

fun interface FI {
    suspend fun call() // suspending now(!!!)
}

fun accept(fi: FI): Int = 1

fun box(): String {
    val fi: suspend () -> Unit = {} // Lambda of a suspending(!!!) functional type
    accept(fi) // ERROR: Type mismatch. Required: FI Found: suspend () → Unit
    return "OK"
}
