/**
 * The original implementation of the parsing functionality in `jlox` was done
 * with static member classes, which Kotlin doesn't properly support (in Native).
 *
 * Instead, this implementation is interface-based, and hopefully a little denser.
 */

/**
 * Represents an expression in an AST
 */
interface Expression {
    fun <T> accept(visitor: ExprVisitor<T>): T
}

interface ExprVisitor<T> {
    fun visitAssignExpr(expr: AssignExpr): T
    fun visitBinaryExpr(expr: BinaryExpr): T
    fun visitCallExpr(expr: CallExpr): T
    fun visitGetExpr(expr: GetExpr): T
    fun visitGroupingExpr(expr: GroupingExpr): T
    fun visitLiteralExpr(expr: LiteralExpr): T
    fun visitLogicalExpr(expr: LogicalExpr): T
    fun visitSetExpr(expr: SetExpr): T
    fun visitSuperExpr(expr: SuperExpr): T
    fun visitThisExpr(expr: ThisExpr): T
    fun visitUnaryExpr(expr: UnaryExpr): T
    fun visitVariableExpr(expr: VariableExpr): T
}

data class AssignExpr(val name: Token, val value: Expression) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitAssignExpr(this)
    }
}

data class BinaryExpr(val left: Expression, val right: Expression, val operator: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitBinaryExpr(this)
    }
}

data class CallExpr(val callee: Expression, val paren: Token, val arguments: List<Expression>) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitCallExpr(this)
    }
}

data class GetExpr(val obj: Expression, val name: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitGetExpr(this)
    }
}

data class GroupingExpr(val expression: Expression) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitGroupingExpr(this)
    }
}

data class LiteralExpr(val value: Any?) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitLiteralExpr(this)
    }
}

data class LogicalExpr(val left: Expression, val right: Expression, val operator: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitLogicalExpr(this)
    }
}

data class SetExpr(val obj: Expression, val name: Token, val value: Expression) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitSetExpr(this)
    }
}

data class SuperExpr(val keyword: Token, val method: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitSuperExpr(this)
    }
}

data class ThisExpr(val keyword: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitThisExpr(this)
    }
}

data class UnaryExpr(val operator: Token, val right: Expression) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitUnaryExpr(this)
    }
}

data class VariableExpr(val name: Token) : Expression {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitVariableExpr(this)
    }
}