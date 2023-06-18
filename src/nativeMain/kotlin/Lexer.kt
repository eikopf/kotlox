import platform.posix.fdopen
import platform.posix.fprintf

val STDERR = fdopen(2, "w")

class Lexer(private var source: String) {

    var hadError = false

    private var lexemeStart = 0
    private var currentChar = 0
    private var line = 0

    fun scanTokens(): List<Token> {

        val tokens = ArrayList<Token>()

        while (!atEnd()) {
            lexemeStart = currentChar
            val result = scanToken()
            if (result != null) {
                tokens.add(
                    Token(
                    result.first,
                    source.substring(lexemeStart,currentChar),
                    result.second,
                    line
                    )
                )
            }
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }


    /**
     * Returns a pair of corresponding to a token and optional literal
     */
    private fun scanToken(): Pair<TokenType, Any?>? {
        when (val c: Char = advance()) {
            // whitespace
            '\n' -> {
                line++
                return null
            }
            ' ', '\r', '\t' -> return null

            // single-char tokens
            '(' -> return Pair(TokenType.LEFT_PAREN, null)
            ')' -> return Pair(TokenType.RIGHT_PAREN, null)
            '{' -> return Pair(TokenType.LEFT_BRACE, null)
            '}' -> return Pair(TokenType.RIGHT_BRACE, null)
            ',' -> return Pair(TokenType.COMMA, null)
            '.' -> return Pair(TokenType.DOT, null)
            '-' -> return Pair(TokenType.MINUS, null)
            '+' -> return Pair(TokenType.PLUS, null)
            ';' -> return Pair(TokenType.SEMICOLON, null)
            '*' -> return Pair(TokenType.STAR, null)

            // 1-2 char tokens
            '!' -> return Pair(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG, null)
            '=' -> return Pair(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL, null)
            '<' -> return Pair(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS, null)
            '>' -> return Pair(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER, null)
            '/' -> {
                // if comment then ignore until end of line
                return if (match('/')) {
                    while ((peek() != '\n') and  !atEnd()) advance()
                    null
                } else {
                    Pair(TokenType.SLASH, null)
                }
            }

            // literals
            '"' -> return Pair(TokenType.STRING, parseStringLiteral())
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> return Pair(TokenType.NUMBER, parseNumberLiteral())

            else -> {
                // handle identifiers, boolean literals, keywords, and nil
                if (c.isLetter()) {
                    return parseWord()
                } else {
                    error(line, "Unexpected character.")
                }
            }
        }

        throw Error("unreachable")
    }

    /**
     * Consumes the next character in the sequence
     */
    private fun advance(): Char {
        return source.toCharArray()[currentChar++]
    }

    /**
     * Conditional variant of advance; used for multi-character tokens
     */
    private fun match(expected: Char): Boolean {
        if (atEnd()) return false
        if (source.toCharArray()[currentChar] != expected) return false

        currentChar++
        return true
    }

    private fun peek(): Char {
        if (atEnd()) return '\u0000' // unicode NULL char
        return source.toCharArray()[currentChar]
    }

    private fun peekNext(): Char {
        if (currentChar + 1 >= source.length) return '\u0000'
        return source.toCharArray()[currentChar + 1]
    }

    private fun parseStringLiteral(): String? {

        // consume string literal
        while ((peek() != '"') and !atEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        // catch unterminated strings
        if (atEnd()) {
            error(line, "Unterminated string.")
            return null
        }

        // consume closing double quote
        advance()

        // trim quotes and return
        return source.substring(lexemeStart + 1, currentChar - 1)
    }

    private fun parseNumberLiteral(): Number? {

        // consume number literal up to decimal
        while (peek().isDigit()) advance()

        // look for decimal
        if ((peek() == '.') and peekNext().isDigit()) {
            // consume decimal
            advance()

            //consume fractional component
            while(peek().isDigit()) advance()
        }

        val result = source.substring(lexemeStart, currentChar).toDoubleOrNull()

        return if (result == null) {
            error(line, "bad numeric literal")
            null
        } else result
    }

    private fun parseWord(): Pair<TokenType, Any?> {

        // consume word
        while (peek().isAlphaNumeric()) advance()

        // check for reserved keyword
        val text = source.substring(lexemeStart, currentChar)
        var type = keywords[text] // equivalent to keywords.get(text)
        if (type == null) type = TokenType.IDENTIFIER

        return Pair(type, text)
    }

    private fun atEnd(): Boolean {
        return currentChar >= source.length
    }

    private fun error(line: Int, message: String) {
        report(line, "", message)
        hadError = true
    }

    private fun report(line: Int, where: String, message: String) {
        fprintf(STDERR, "%s\n", "[line $line] Error$where: $message")
    }
}

enum class TokenType {
    // single-char tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // 1-2 char tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // literals
    IDENTIFIER, STRING, NUMBER,

    // keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // special
    EOF
}

val keywords = mapOf(
    "and" to TokenType.AND,
    "class" to TokenType.CLASS,
    "else" to TokenType.ELSE,
    "false" to TokenType.FALSE,
    "for" to TokenType.FOR,
    "fun" to TokenType.FUN,
    "if" to TokenType.IF,
    "nil" to TokenType.NIL,
    "or" to TokenType.OR,
    "print" to TokenType.PRINT,
    "return" to TokenType.RETURN,
    "super" to TokenType.SUPER,
    "this" to TokenType.THIS,
    "true" to TokenType.TRUE,
    "var" to TokenType.VAR,
    "while" to TokenType.WHILE
    )

data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int) {
    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}

fun Char.isAlphaNumeric(): Boolean {
    return this.isDigit() or this.isLetter()
}
