package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import kotlin.reflect.KClass

fun <T> Observable<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Observable<T> =
    observable { emitter ->
        val retry = Retry(emitter, predicate)

        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {

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

fun <T> Observable<T>.retry(throwableType: KClass<out Throwable>): Observable<T> =
    retry { _, throwable -> throwableType.isInstance(throwable) }