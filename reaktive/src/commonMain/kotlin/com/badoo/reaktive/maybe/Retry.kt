package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Maybe] signals `onError`, re-subscribes to the [Maybe] if the [predicate] returns `true`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#retry-io.reactivex.functions.BiPredicate-).
 */
fun <T> Maybe<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, CompleteCallback by emitter {
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
 * When the [Maybe] signals `onError`, re-subscribes to the [Maybe], up to [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#retry-long-).
 */
fun <T> Maybe<T>.retry(times: Int): Maybe<T> =
    retry { attempt, _ -> attempt < times }
