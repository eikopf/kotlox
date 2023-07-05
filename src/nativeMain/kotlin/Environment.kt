class Environment(private val enclosing: Environment? = null) {
    private val values: MutableMap<String, Any?> = HashMap()     // stores top-level bindings and environments

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any? {
       return values.getOrElse(name.lexeme) {
           if (enclosing != null) return enclosing.get(name) // recursive step into enclosing environment
           throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
       }
    }

    fun assign(name: Token, value: Any?) {
        if (name.lexeme in values.keys) {
            values[name.lexeme] = value
            return
        }

        // no implicit variable declaration; if the variable doesn't exist then it's an error
        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }
}