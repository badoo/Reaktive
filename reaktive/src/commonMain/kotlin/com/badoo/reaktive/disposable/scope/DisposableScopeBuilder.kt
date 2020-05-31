package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.annotations.UseReturnValue
import kotlin.js.JsName

/**
 * Creates a new instance of [DisposableScope]
 */
@Suppress("FunctionName")
@JsName("disposableScope")
@UseReturnValue
fun DisposableScope(): DisposableScope = DisposableScopeImpl()

/**
 * Creates a new instance of [DisposableScope] and calls the provided `block` on it
 */
@UseReturnValue
inline fun disposableScope(block: DisposableScope.() -> Unit): DisposableScope =
    DisposableScope().apply(block)
