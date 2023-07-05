/**
 * Parses a list of tokens into an expression.
 *
 * The specific expression precedence order
 * found in Lox is enforced here by the structure
 * of the functions which parse each kind of
 * basic expression.
 */
class Parser(private val tokens: List<Token>) {
    private var current: Int = 0

    fun parse(): List<Statement>? {
        val statements = ArrayList<Statement>()
        while (!atEnd()) statements.add(declaration() ?: return null)
        return statements
    }

    private fun declaration(): Statement? {
        try {
            if (match(TokenType.VAR)) return varDeclaration()
            return statement()
        } catch (err: ParseError) {
            synchronize()
            return null
        }
    }

    private fun varDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        val initializer = if (match(TokenType.EQUAL)) expression() else null

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration")
        return VarStmt(name, initializer)
    }

    private fun statement(): Statement {
        if (match(TokenType.PRINT)) return printStatement()
        if (match(TokenType.LEFT_BRACE)) return BlockStmt(block())

        return expressionStatement()
    }

    private fun block(): List<Statement> {
        val statements: MutableList<Statement> = ArrayList()
        while (!check(TokenType.RIGHT_BRACE) and !atEnd()) statements.add(declaration() ?: throw ParseError())

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun printStatement(): Statement {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return PrintStmt(value)
    }

    private fun expressionStatement(): Statement {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return ExpressionStmt(expr)
    }

    private fun expression(): Expression {
        return assignment()
    }

    private fun assignment(): Expression {
        val expr = equality()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment() // recursive invocation handles nested assignments, i.e. 'a.b.c = true'

            if (expr is VariableExpr) { // implicit safe-cast to VariableExpr
                val name = expr.name
                return AssignExpr(name, value)
            }

            error(equals, "Invalid assignment target.")
        }

        return expr
    }

    private fun equality(): Expression {
        var expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = BinaryExpr(expr, right, operator)
        }

        return expr
    }

    private fun comparison(): Expression {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = BinaryExpr(expr, right, operator)
        }

        return expr
    }

    private fun term(): Expression {
        var expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = BinaryExpr(expr, right, operator)
        }

        return expr
    }

    private fun factor(): Expression {
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = BinaryExpr(expr, right, operator)
        }

        return expr
    }

    private fun unary(): Expression {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return UnaryExpr(operator, right)
        }

        return primary()
    }

    private fun primary(): Expression {
        if (match(TokenType.FALSE)) return LiteralExpr(false)
        if (match(TokenType.TRUE)) return LiteralExpr(true)
        if (match(TokenType.NIL)) return LiteralExpr(null)
        if (match(TokenType.NUMBER, TokenType.STRING)) return LiteralExpr(previous().literal)
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return GroupingExpr(expr)
        }
        if (match(TokenType.IDENTIFIER)) return VariableExpr(previous())
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return GroupingExpr(expr)
        }
        throw parseError(peek(), "Expect expression.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun synchronize() {
        advance()

        while (!atEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS, TokenType.FUN, TokenType.VAR,
                TokenType.FOR, TokenType.IF, TokenType.WHILE,
                TokenType.PRINT, TokenType.RETURN -> return
                else -> advance()
            }
        }
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw parseError(peek(), message)
    }

    private fun check(type: TokenType): Boolean {
        if (atEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!atEnd()) current++
        return previous()
    }

    private fun atEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }
}

private fun parseError(token: Token, message: String): ParseError {
    error(token, message)
    return ParseError()
}

class ParseError : RuntimeException()