import platform.posix.exit
import okio.Path.Companion.toPath

fun main(args: Array<String>) {
    defaultBehaviour(args)
}

private fun defaultBehaviour(args: Array<String>) {
    if (args.size > 2) {            // bad invocation
        println("Usage: kotlox [script]")
        exit(64)
    } else if (args.size == 1) {    // file mode
        runFile(args[0].toPath())
    } else if (args.size == 2) {   // tokenize
        if (args[0] == "tokenize") tokenizeFile(args[1].toPath())
        if (args[0] == "statements") printFileStatements(args[1].toPath())
    } else {                        // repl mode
        runPrompt()
    }
}