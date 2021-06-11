package com.badoo.reaktive.completable

import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Completable] signals `onError`, re-subscribes to the [Completable] if the [predicate] returns `true`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#retry-io.reactivex.functions.BiPredicate-).
 */
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

/**
 * When the [Completable] signals `onError`, re-subscribes to the [Completable], up to [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#retry-long-).
 */
fun Completable.retry(times: Int): Completable =
    retry { attempt, _ -> attempt < times }
