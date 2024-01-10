package com.badoo.reaktive.scheduler

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Any =
    js("setTimeout(task, delayMillis)")

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): Any =
    js("setInterval(task, delayMillis)")

internal actual fun jsClearTimeout(id: Any) {
    js("clearTimeout(id)")
}

internal actual fun jsClearInterval(id: Any) {
    js("clearInterval(id)")
}
