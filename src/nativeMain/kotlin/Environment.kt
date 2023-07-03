class Environment {
    private val values: MutableMap<String, Any?> = HashMap()     // stores top-level bindings and environments

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any? {
       return values.getOrElse(name.lexeme) {
           throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
       }
    }
}