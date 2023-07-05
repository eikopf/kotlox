# Kotlox
`kotlox` is a Kotlin/Native port of Bob Nystrom's `jlox` AST-Walk interpreter
from his book *Crafting Interpreters*. The aim of this project
is (basically) to relearn Kotlin, as well as to learn how
Kotlin/Native works, and more generally out of general
curiosity regarding interpreters.

> I had originally planned to port `jlox` to OCaml, but it turns
> out trying to learn a new language *and* a new paradigm at the
> same time, all while re-architecting an OOP program into FP,
> is quite difficult.
> 
> Who knew?

## Dependencies
| Dependency                                                      | Purpose                                                       |
|-----------------------------------------------------------------|---------------------------------------------------------------|
 | [`okio`](https://square.github.io/okio/)                        | A Kotlin/Native library for POSIX file IO                     |
| [`kotlinx-datetime`](https://github.com/Kotlin/kotlinx-datetime) | A multiplatform Kotlin library for working with date and time |