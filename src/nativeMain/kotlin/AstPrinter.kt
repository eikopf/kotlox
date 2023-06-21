class AstPrinter : ExprVisitor<String> {

    fun print(expr: Expression): String {
        return expr.accept(this)
    }

    private fun parenthesize(name: String, vararg expr: Expression): String {
        val acceptString = " " + expr.joinToString(" ") { it.accept(this) }
        return "($name$acceptString)"
    }

    override fun visitAssignExpr(expr: AssignExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpr(expr: BinaryExpr): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitCallExpr(expr: CallExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: GetExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: GroupingExpr): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: LiteralExpr): String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitLogicalExpr(expr: LogicalExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: SetExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: SuperExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: ThisExpr): String {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: UnaryExpr): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitVariableExpr(expr: VariableExpr): String {
        TODO("Not yet implemented")
    }
}