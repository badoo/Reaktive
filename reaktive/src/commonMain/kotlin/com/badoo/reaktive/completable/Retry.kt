package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.reflect.KClass

fun Completable.retry(predicate: (Int, Throwable) -> Boolean = { _, _ -> true }): Completable =
    completable { emitter ->
        val attempt = AtomicInt(-1)

        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompleteCallback by emitter {

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch(
                        { predicate(attempt.addAndGet(1), error) },
                        { CompositeException(error, it) }
                    ) { shouldRetry ->
                        if (shouldRetry) {
                            this@retry.subscribeSafe(this)
                        } else {
                            emitter.onError(error)
                        }
                    }
                }
            }
        )
    }

fun Completable.retry(times: Int): Completable = retry { attempt, _ -> attempt < times }

fun Completable.retry(throwableType: KClass<out Throwable>) =
    retry { _, throwable -> throwableType.isInstance(throwable) }