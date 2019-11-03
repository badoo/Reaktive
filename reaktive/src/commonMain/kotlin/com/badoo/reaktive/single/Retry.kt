package com.badoo.reaktive.single

import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable

fun <T> Single<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter {
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

fun <T> Single<T>.retry(times: Int): Single<T> =
    retry { attempt, _ -> attempt < times }
