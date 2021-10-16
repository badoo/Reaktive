package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [mapper] with the value emitted by the [Single] and subscribes to the returned inner [Single].
 * Emits the value from the inner [Single].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMap-io.reactivex.functions.Function-).
 */
fun <T, R> Single<T>.flatMap(mapper: (T) -> Single<R>): Single<R> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch {
                        mapper(value).subscribe(
                            object : SingleObserver<R>, Observer by this, SingleCallbacks<R> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * Calls the [mapper] with the value emitted by the [Single] and subscribes to the returned inner [Single].
 * When the inner [Single] emits, calls the [resultSelector] function with the original and the inner values and emits the result.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMap-io.reactivex.functions.Function-).
 */
fun <T, U, R> Single<T>.flatMap(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Single<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
