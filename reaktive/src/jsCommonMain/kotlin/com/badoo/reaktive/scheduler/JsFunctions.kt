package com.badoo.reaktive.scheduler

internal expect fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Any

internal expect fun jsSetInterval(task: () -> Unit, delayMillis: Int): Any

internal expect fun jsClearTimeout(id: Any)

internal expect fun jsClearInterval(id: Any)
