package com.badoo.reaktive.scheduler

internal expect fun jsSetTimeout(task: () -> Unit, delayMillis: Int): Int

internal expect fun jsSetInterval(task: () -> Unit, delayMillis: Int): Int

internal expect fun jsClearTimeout(id: Int)

internal expect fun jsClearInterval(id: Int)
