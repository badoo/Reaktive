package com.badoo.reaktive.completable

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompletableCallbacks by emitter {
                private val retry = Retry(emitter, predicate)

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    retry.onError(error) { this@retry.subscribeSafe(this) }
                }
            }
        )
    }

fun Completable.retry(times: Int): Completable =
    retry { attempt, _ -> attempt < times }
