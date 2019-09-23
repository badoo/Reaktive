package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Observable<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Observable<T> =
    observable { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
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

fun <T> Observable<T>.retry(times: Int): Observable<T> =
    retry { attempt, _ -> attempt < times }