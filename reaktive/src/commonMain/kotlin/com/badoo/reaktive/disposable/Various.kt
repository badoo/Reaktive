package com.badoo.reaktive.disposable

@Suppress("FunctionName")
expect inline fun Disposable(crossinline onDispose: () -> Unit): Disposable

@Suppress("FunctionName")
expect fun Disposable(): Disposable
