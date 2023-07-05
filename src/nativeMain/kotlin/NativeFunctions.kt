import kotlinx.datetime.Clock

class LoxClock : LoxCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Long {
        return Clock.System.now().epochSeconds
    }

    override fun toString(): String {
        return "<native fn>"
    }
}