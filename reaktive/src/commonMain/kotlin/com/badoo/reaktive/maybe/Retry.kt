package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Maybe<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Maybe<T> =
    maybe { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
                private val retry = Retry(emitter, predicate)

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    retry.onError(error) { this@retry.subscribeSafe(this) }
                }
            }
        )
    }

fun <T> Maybe<T>.retry(times: Int): Maybe<T> =
    retry { attempt, _ -> attempt < times }