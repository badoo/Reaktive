package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

/**
 * Represents a scope of [Disposable]s. All scoped [Disposable]s are disposed when the [DisposableScope] is disposed.
 */
@ExperimentalReaktiveApi
interface DisposableScope : Disposable {

    /**
     * Adds this [Disposable] to the scope
     */
    @ExperimentalReaktiveApi
    fun <T : Disposable> T.scope(): T

    /**
     * Adds this [CompleteCallback] to the scope and calls its [CompleteCallback.onComplete] callback when disposed
     */
    @ExperimentalReaktiveApi
    fun <T : CompleteCallback> T.scope(): T

    /**
     * Same as [Observable.subscribe][com.badoo.reaktive.observable.subscribe] but also adds the [Disposable] to the scope
     */
    @ExperimentalReaktiveApi
    fun <T> Observable<T>.subscribeScoped(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onNext: ((T) -> Unit)? = null
    ): Disposable

    /**
     * Same as [Single.subscribe][com.badoo.reaktive.single.subscribe] but also adds the [Disposable] to the scope
     */
    @ExperimentalReaktiveApi
    fun <T> Single<T>.subscribeScoped(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null
    ): Disposable

    /**
     * Same as [Maybe.subscribe][com.badoo.reaktive.maybe.subscribe] but also adds the [Disposable] to the scope
     */
    @ExperimentalReaktiveApi
    fun <T> Maybe<T>.subscribeScoped(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null
    ): Disposable

    /**
     * Same as [Completable.subscribe][com.badoo.reaktive.completable.subscribe] but also adds the [Disposable] to the scope
     */
    @ExperimentalReaktiveApi
    fun Completable.subscribeScoped(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Disposable
}
