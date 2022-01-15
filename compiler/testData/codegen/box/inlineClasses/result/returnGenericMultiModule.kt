// WITH_STDLIB

// MODULE: lib
interface Operation<T> {
    fun performOperation(): T
}

object ResultOperation : Operation<Result<Int>> {
    override fun performOperation(): Result<Int> {
        return Result.success(1)
    }
}

// MODULE: main(lib)
fun box(): String {
    val x = ResultOperation.performOperation()
    if ("$x" != "Success(1)") return "$x"
    return "OK"
}
