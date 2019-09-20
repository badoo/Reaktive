package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import kotlin.reflect.KClass

fun <T> Maybe<T>.retry(predicate: (Int, Throwable) -> Boolean = { _, _ -> true }): Maybe<T> =
    maybe { emitter ->
        val retry = Retry(emitter, predicate)

        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {

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

fun <T> Maybe<T>.retry(throwableType: KClass<out Throwable>): Maybe<T> =
    retry { _, throwable -> throwableType.isInstance(throwable) }