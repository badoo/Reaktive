package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable

/**
 * Adds the provided `block` callback to the scope.
 * The callback will be called when the scope is disposed.
 */
@ExperimentalReaktiveApi
inline fun DisposableScope.doOnDispose(crossinline block: () -> Unit) {
    Disposable(block).scope()
}
