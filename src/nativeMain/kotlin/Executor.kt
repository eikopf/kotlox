import okio.FileSystem
import okio.Path
import platform.posix.exit
import platform.posix.fdopen
import platform.posix.fprintf

val STDERR = fdopen(2, "w")

class Executor(private val interpreter: Interpreter) {

    // in its original implementation, the jlox executor would
    // handle errors during the code pipeline with a single hadError
    // instance variable; this has been refactored into a set of
    // runtime errors that can be thrown in individual parts of
    // the pipeline

    fun run(source: String) {
        val lexer = Lexer(source)
        val tokens = lexer.scanTokens() ?: throw LexError()

        val parser = Parser(tokens)
        val statements: List<Statement> = parser.parse() ?: throw ParseError()

        interpreter.interpret(statements) // throws InterpreterError
    }
}

/**
 * Takes a file path and executes the interpreter on it
 */
fun runFile(path: Path) {
    // there's probably a nice idiomatic way to refactor this
    val contents = FileSystem.SYSTEM.read(path) {
        readUtf8()
    }

    // generate a new interpreter, since it only exists over a single file
    val executor = Executor(Interpreter())

    try {
        executor.run(contents)
    } catch (err: LexError) {
        exit(65)
    } catch (err: ParseError) {
        exit(65)
    } catch (err: InterpreterError) {
        exit(70)
    }
}

/**
 * Runs in REPL mode
 */
fun runPrompt() {

    val interpreter = Interpreter()
    val executor = Executor(interpreter)

    println("> kotlox repl")
    while (true) {
        // begin prompt line
        print("> ")

        // read
        val line = readlnOrNull() ?: return
        if (line.isEmpty()) return

        // eval
        try {
            executor.run(line)
        } catch (err: RuntimeException){
            println("syntax error: ${err.message ?: err}")
        }

    }   // loop
}

fun report(line: Int, where: String, message: String) {
    fprintf(STDERR, "%s\n", "[line $line] Error$where: $message")
}

fun error(token: Token, message: String) {
    if (token.type == TokenType.EOF) {
        report(token.line, " at end", message)
    } else {
        report(token.line, "at '${token.lexeme}'", message)
    }
}