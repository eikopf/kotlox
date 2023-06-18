import platform.posix.exit
import okio.Path.Companion.toPath

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: kotlox [script]")
        exit(64)
    } else if (args.size == 1) {
        runFile(args[0].toPath())
    } else {
        runPrompt()
    }
}


