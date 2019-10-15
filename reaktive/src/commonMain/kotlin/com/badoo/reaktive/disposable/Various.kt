package com.badoo.reaktive.disposable

import kotlin.js.JsName

@Deprecated(
    message = "Use Disposable instead",
    replaceWith = ReplaceWith("Disposable(onDispose)", "com.badoo.reaktive.disposable.Disposable")
)
@JsName("disposableDeprecated")
inline fun disposable(crossinline onDispose: () -> Unit = {}): Disposable = Disposable(onDispose)

expect inline fun Disposable(crossinline onDispose: () -> Unit): Disposable

expect fun Disposable(): Disposable
