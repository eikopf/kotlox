import okio.FileSystem
import okio.Path
import platform.posix.exit


class Interpreter {

    fun run(tokens: List<Token>) {
        // temporary debugging statement
        tokens.forEach { println(it) }
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

    val lexer = Lexer(contents)
    val tokens = lexer.scanTokens()
    val interpreter = Interpreter()

    if (lexer.hadError) exit(65)
    else interpreter.run(tokens)
}

/**
 * Runs in REPL mode
 */
fun runPrompt() {

    val interpreter = Interpreter()

    println("> kotlox repl")
    while (true) {
        // begin prompt line
        print("> ")

        // read
        val line = readlnOrNull() ?: return
        if (line.isEmpty()) return

        // eval
        val lexer = Lexer(line)
        val tokens = lexer.scanTokens()
        if (lexer.hadError) {
            println("syntax error")
            lexer.hadError = false
        }
        else interpreter.run(tokens)

    }   // loop
}