package com.badoo.reaktive.scheduler

import com.badoo.reaktive.global.external.globalThis

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Any =
    globalThis.setTimeout(task, delayMillis)

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): Any =
    globalThis.setInterval(task, delayMillis)

internal actual fun jsClearTimeout(id: Any) {
    globalThis.clearTimeout(id)
}

internal actual fun jsClearInterval(id: Any) {
    globalThis.clearInterval(id)
}
