import platform.posix.fprintf

class Interpreter : ExprVisitor<Any?>, StmtVisitor<Unit> {  // can't use java's Void in Kotlin/Native

    private var environment = Environment()

    fun interpret(statements: List<Statement>) {
        try {
            statements.forEach {
                execute(it)
            }
        } catch (err: RuntimeError) {
            runtimeError(err)
        }
    }

    private fun execute(stmt: Statement) {
        stmt.accept(this)
    }

    private fun executeBlock(statements: List<Statement>, environment: Environment) {
        val previous = this.environment // store outer (current) scope

        try {
            this.environment = environment // move to inner scope
            statements.forEach { execute(it) }  // sequentially execute the block

        } finally {
            this.environment = previous // move back into outer scope
        }
    }

    private fun evaluate(expr: Expression): Any? {
        return expr.accept(this)
    }

    override fun visitBlockStmt(stmt: BlockStmt) {
        executeBlock(stmt.statements, Environment(environment))
    }

    override fun visitClassStmt(stmt: ClassStmt) {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: ExpressionStmt) {
        evaluate(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: FunctionStmt) {
        TODO("Not yet implemented")
    }

    override fun visitIfStmt(stmt: IfStmt) {
        if (truthy(evaluate(stmt.condition))) execute(stmt.thenBranch)
        else execute(stmt.elseBranch ?: return) // return if elseBranch is null
    }

    override fun visitPrintStmt(stmt: PrintStmt) {
        val value: Any? = evaluate(stmt.expression)
        println(stringify(value))
    }

    override fun visitReturnStmt(stmt: ReturnStmt) {
        TODO("Not yet implemented")
    }

    override fun visitVarStmt(stmt: VarStmt) {
        val value = if (stmt.initializer != null) evaluate(stmt.initializer) else null
        environment.define(stmt.name.lexeme, value)
    }

    override fun visitWhileStmt(stmt: WhileStmt) {
        while (truthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
    }

    override fun visitAssignExpr(expr: AssignExpr): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
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
        val left = evaluate(expr.left)

        if ((expr.operator.type == TokenType.OR) and (truthy(left))) return left
        else if (!truthy(left)) return left

        return evaluate(expr.right)
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
        return environment.get(expr.name)
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

class RuntimeError(val token: Token, message: String) : RuntimeException(message)
class InterpreterError : RuntimeException()

private fun runtimeError(err: RuntimeError) {
    fprintf(STDERR, "%s\n", "${err.message}\n[line ${err.token.line}")
    throw InterpreterError()
}