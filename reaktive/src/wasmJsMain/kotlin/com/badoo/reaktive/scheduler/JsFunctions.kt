package com.badoo.reaktive.scheduler

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Int =
    js("setTimeout(task, delayMillis)")

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): Int =
    js("setInterval(task, delayMillis)")

internal actual fun jsClearTimeout(id: Int) {
    js("clearTimeout(id)")
}

internal actual fun jsClearInterval(id: Int) {
    js("clearInterval(id)")
}
