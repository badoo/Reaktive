package com.badoo.reaktive.completable

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import kotlin.reflect.KClass

fun Completable.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Completable =
    completable { emitter ->
        val retry = Retry(emitter, predicate)

        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by emitter {

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    retry.onError(error) { this@retry.subscribeSafe(this) }
                }
            }
        )
    }

fun Completable.retry(times: Int): Completable =
    retry { attempt, _ -> attempt < times }

fun Completable.retry(throwableType: KClass<out Throwable>): Completable =
    retry { _, throwable -> throwableType.isInstance(throwable) }