package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun Completable.doOnBeforeSubscribe(action: (Disposable) -> Unit): Completable =
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
                override fun onSubscribe(disposable: Disposable) {
                    action(disposable)
                    observer.onSubscribe(disposable)
                }
            }
        )
    }

fun Completable.doOnBeforeComplete(action: () -> Unit): Completable =
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
                override fun onComplete() {
                    action()
                    observer.onComplete()
                }
            }
        )
    }

fun Completable.doOnBeforeError(consumer: (Throwable) -> Unit): Completable =
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun Completable.doOnBeforeTerminate(action: () -> Unit): Completable =
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
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
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = action))
                }
            }
        )
    }

fun Completable.doOnBeforeFinally(action: () -> Unit): Completable =
    completable { observer ->
        subscribeSafe(
            object : CompletableObserver by observer {
                private val lock = newLock()
                private var isFinished = false

                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = ::onFinally))
                }

                override fun onComplete() {
                    onFinally()
                    observer.onComplete()
                }

                override fun onError(error: Throwable) {
                    onFinally()
                    observer.onError(error)
                }

                private fun onFinally() {
                    if (isFinished) {
                        return
                    }

                    lock.synchronized {
                        if (isFinished) {
                            return
                        }
                        isFinished = true
                    }

                    action()
                }
            }
        )
    }
