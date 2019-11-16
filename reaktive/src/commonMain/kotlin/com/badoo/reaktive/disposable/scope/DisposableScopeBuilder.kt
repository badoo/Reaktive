package com.badoo.reaktive.disposable.scope

import kotlin.js.JsName

/**
 * Creates a new instance of [DisposableScope]
 */
@Suppress("FunctionName")
@JsName("disposableScope")
fun DisposableScope(): DisposableScope = DisposableScopeImpl()

/**
 * Creates a new instance os [DisposableScope] and calls the provided `block` on it
 */
inline fun disposableScope(block: DisposableScope.() -> Unit): DisposableScope =
    DisposableScope().apply(block)
