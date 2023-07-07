class LoxFunction(private val declaration: FunctionStmt, private val closure: Environment) : LoxCallable {
    override fun arity(): Int = declaration.params.size
    override fun toString(): String = "<fn ${declaration.name.lexeme}>"

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)

        for (i in 0 until declaration.params.size) environment.define(
            declaration.params[i].lexeme,
            arguments[i]
            )

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }
}

// the original implementation uses a JVM RuntimeException that suppresses the stacktrace
// no such equivalent exists in Kotlin/Native, so I'm just hoping this has no performance impact.
class Return(val value: Any?) : RuntimeException(null, null)