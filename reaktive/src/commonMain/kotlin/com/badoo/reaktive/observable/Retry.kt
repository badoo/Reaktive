package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.operator.Retry
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns an [Observable] that automatically resubscribes to this [Observable] if it signals `onError`
 * and the [predicate] returns `true` for that specific [Throwable] and `attempt`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#retry-io.reactivex.functions.BiPredicate-).
 */
fun <T> Observable<T>.retry(predicate: (attempt: Int, Throwable) -> Boolean = { _, _ -> true }): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, CompleteCallback by emitter {
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
 * Returns an [Observable] that automatically resubscribes to this [Observable] at most [times] times if it signals `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#retry-long-).
 */
fun <T> Observable<T>.retry(times: Int): Observable<T> =
    retry { attempt, _ -> attempt < times }
