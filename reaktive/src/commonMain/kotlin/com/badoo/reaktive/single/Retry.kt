package com.badoo.reaktive.single

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import kotlin.reflect.KClass

fun <T> Single<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Single<T> =
    single { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, SingleCallbacks<T> by emitter {
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

fun <T> Single<T>.retry(times: Int): Single<T> =
    retry { attempt, _ -> attempt < times }

fun <T> Single<T>.retry(throwableType: KClass<out Throwable>): Single<T> =
    retry { _, throwable -> throwableType.isInstance(throwable) }