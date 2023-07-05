
interface Statement {
    fun <T> accept(visitor: StmtVisitor<T>): T
}

interface StmtVisitor<T> {
    fun visitBlockStmt(stmt: BlockStmt): T
    fun visitClassStmt(stmt: ClassStmt): T
    fun visitExpressionStmt(stmt: ExpressionStmt): T
    fun visitFunctionStmt(stmt: FunctionStmt): T
    fun visitIfStmt(stmt: IfStmt): T
    fun visitPrintStmt(stmt: PrintStmt): T
    fun visitReturnStmt(stmt: ReturnStmt): T
    fun visitVarStmt(stmt: VarStmt): T
    fun visitWhileStmt(stmt: WhileStmt): T
}

data class BlockStmt(val statements: List<Statement>) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitBlockStmt(this)
    }
}

data class ClassStmt(val name: Token, val superclass: VariableExpr, val methods: List<FunctionStmt>) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitClassStmt(this)
    }
}

data class ExpressionStmt(val expression: Expression) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitExpressionStmt(this)
    }
}

data class FunctionStmt(val name: Token, val params: List<Token>, val body: List<Statement>) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitFunctionStmt(this)
    }
}

data class IfStmt(val condition: Expression, val thenBranch: Statement, val elseBranch: Statement?) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitIfStmt(this)
    }
}

data class PrintStmt(val expression: Expression) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitPrintStmt(this)
    }
}

data class ReturnStmt(val keyword: Token, val value: Expression) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitReturnStmt(this)
    }
}

data class VarStmt(val name: Token, val initializer: Expression?) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitVarStmt(this)
    }
}

data class WhileStmt(val condition: Expression, val body: Statement) : Statement {
    override fun <T> accept(visitor: StmtVisitor<T>): T {
        return visitor.visitWhileStmt(this)
    }
}