package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.subscribe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.subscribe

@OptIn(ExperimentalReaktiveApi::class)
internal class DisposableScopeImpl : DisposableScope, CompositeDisposable() {

    override fun <T : Disposable> T.scope(): T {
        purge()
        add(this)

        return this
    }

    override fun <T : CompleteCallback> T.scope(): T {
        Disposable(::onComplete).scope()

        return this
    }

    override fun <T> Observable<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onNext: ((T) -> Unit)?
    ): Disposable =
        subscribe(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete,
            onNext = onNext
        )
            .scope()

    override fun <T> Single<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        subscribe(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onSuccess = onSuccess
        )
            .scope()

    override fun <T> Maybe<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        subscribe(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete,
            onSuccess = onSuccess
        )
            .scope()

    override fun Completable.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?
    ): Disposable =
        subscribe(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete
        )
            .scope()
}
