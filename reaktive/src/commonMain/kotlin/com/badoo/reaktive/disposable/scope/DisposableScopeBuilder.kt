package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.annotations.UseReturnValue
import kotlin.js.JsName

/**
 * Creates a new instance of [DisposableScope]
 */
@Suppress("FunctionName")
@ExperimentalReaktiveApi
@JsName("disposableScope")
@UseReturnValue
fun DisposableScope(): DisposableScope = DisposableScopeImpl()

/**
 * Creates a new instance of [DisposableScope] and calls the provided `block` on it
 */
@ExperimentalReaktiveApi
@UseReturnValue
inline fun disposableScope(block: DisposableScope.() -> Unit): DisposableScope =
    DisposableScope().apply(block)
