import platform.posix.fprintf

class Interpreter : ExprVisitor<Any?> {

    fun interpret(expr: Expression) {
        try {
            val value = evaluate(expr)
            println(stringify(value))
        } catch (err: RuntimeError) {
            runtimeError(err)
        }
    }

    private fun evaluate(expr: Expression): Any? {
        return expr.accept(this)
    }

    override fun visitAssignExpr(expr: AssignExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpr(expr: BinaryExpr): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        try {
            return when (expr.operator.type) {

                // algebraic operators
                TokenType.MINUS -> left as Double - right as Double
                TokenType.SLASH -> left as Double / right as Double
                TokenType.STAR -> left as Double * right as Double
                TokenType.PLUS -> {
                    if ((left is Double) and (right is Double)) left as Double + right as Double
                    else if ((left is String) and (right is String)) left as String + right as String
                    else throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
                }

                // logical operators
                TokenType.GREATER -> left as Double > right as Double
                TokenType.GREATER_EQUAL -> left as Double >= right as Double
                TokenType.LESS -> (left as Double) < (right as Double) // without brackets this is a syntax error
                TokenType.LESS_EQUAL -> left as Double <= right as Double
                TokenType.BANG_EQUAL -> !equal(left, right)
                TokenType.EQUAL_EQUAL -> equal(left, right)

                else -> null // unreachable
            }
        } catch (err: ClassCastException) {
            throw RuntimeError(expr.operator, "Operands must be numbers.")
        } catch (err: RuntimeError) {
            throw err
        }
    }

    override fun visitCallExpr(expr: CallExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: GetExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: GroupingExpr): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: LiteralExpr): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: LogicalExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: SetExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: SuperExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: ThisExpr): Any? {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: UnaryExpr): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> -1 * right as Double
            TokenType.BANG -> !truthy(right)
            else -> null // unreachable
        }
    }

    override fun visitVariableExpr(expr: VariableExpr): Any? {
        TODO("Not yet implemented")
    }
}

private fun truthy(any: Any?): Boolean {
    if (any == null) return false
    if (any is Boolean) return any
    return true
}

private fun equal(a: Any?, b: Any?): Boolean {
    if ((a == null) and (b == null)) return true
    if (a == null) return false

    return a == b // kotlin does referential equality with triple-equals (===)
}

private fun stringify(any: Any?): String {
    if (any == null) return "nil"

    if (any is Double) return buildString {
            append(any.toString())
            removeSuffix(".0")
    }

    return any.toString()
}

private class RuntimeError(val token: Token, message: String) : RuntimeException(message)
class InterpreterError : RuntimeException()

private fun runtimeError(err: RuntimeError) {
    fprintf(STDERR, "%s\n", "${err.message}\n[line ${err.token.line}")
    throw InterpreterError()
}