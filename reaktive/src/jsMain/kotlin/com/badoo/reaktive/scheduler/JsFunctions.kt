package com.badoo.reaktive.scheduler

import com.badoo.reaktive.global.external.globalThis

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Int =
    globalThis.setTimeout(task, delayMillis)

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): Int =
    globalThis.setInterval(task, delayMillis)

internal actual fun jsClearTimeout(id: Int) {
    globalThis.clearTimeout(id)
}

internal actual fun jsClearInterval(id: Int) {
    globalThis.clearInterval(id)
}
