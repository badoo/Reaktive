package com.badoo.reaktive.scheduler

import kotlinx.browser.window

internal actual fun jsSetTimeout(task: () -> Unit, delayMillis: Int): TimeoutId =
    window.setTimeout({ task().toJsReference() }, delayMillis)

internal actual fun jsSetInterval(task: () -> Unit, delayMillis: Int): TimeoutId =
    window.setInterval({ task().toJsReference() }, delayMillis)

internal actual fun jsClearTimeout(id: TimeoutId) {
    window.clearTimeout(id)
}

internal actual fun jsClearInterval(id: TimeoutId) {
    window.clearInterval(id)
}
