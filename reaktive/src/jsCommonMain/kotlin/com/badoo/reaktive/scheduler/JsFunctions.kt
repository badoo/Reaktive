package com.badoo.reaktive.scheduler

internal expect fun jsSetTimeout(task: () -> Unit, delayMillis: Int): TimeoutId

internal expect fun jsSetInterval(task: () -> Unit, delayMillis: Int): TimeoutId

internal expect fun jsClearTimeout(id: TimeoutId)

internal expect fun jsClearInterval(id: TimeoutId)
