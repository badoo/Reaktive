package com.badoo.reaktive.scheduler

import com.badoo.reaktive.global.external.globalThis

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): TimeoutId =
    TimeoutId(globalThis.setTimeout(task, delayMillis))

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): TimeoutId =
    TimeoutId(globalThis.setInterval(task, delayMillis))

internal actual fun jsClearTimeout(id: TimeoutId) {
    globalThis.clearTimeout(id.id)
}

internal actual fun jsClearInterval(id: TimeoutId) {
    globalThis.clearInterval(id.id)
}
