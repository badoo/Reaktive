package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun Completable.doOnBeforeSubscribe(action: (Disposable) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                    action(disposable)
                }
            }
        )
    }

fun Completable.doOnBeforeComplete(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    action()
                    observer.onComplete()
                }
            }
        )
    }

fun Completable.doOnBeforeError(consumer: (Throwable) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompleteCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun Completable.doOnBeforeTerminate(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    action()
                    observer.onComplete()
                }

                override fun onError(error: Throwable) {
                    action()
                    observer.onError(error)
                }
            }
        )
    }

fun Completable.doOnBeforeDispose(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = action))

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun Completable.doOnBeforeFinally(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val isFinished = AtomicReference(false)

        fun onFinally() {
            @Suppress("BooleanLiteralArgument") // Not allowed for expected classes
            if (isFinished.compareAndSet(false, true)) {
                action()
            }
        }

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = ::onFinally))

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    onFinally()
                    observer.onComplete()
                }

                override fun onError(error: Throwable) {
                    onFinally()
                    observer.onError(error)
                }
            }
        )
    }
