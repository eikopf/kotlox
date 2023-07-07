interface LoxCallable {
    fun arity(): Int
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}

class LoxFunction(private val declaration: FunctionStmt) : LoxCallable {
    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>) {
        val environment = Environment(globals)

        for (i in 0 until declaration.params.size) environment.define(
            declaration.params[i].lexeme,
            arguments[i]
            )

        interpreter.executeBlock(declaration.body, environment)
    }
}