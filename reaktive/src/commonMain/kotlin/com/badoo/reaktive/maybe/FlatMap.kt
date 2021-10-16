package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [mapper] with the value emitted by the [Maybe] and subscribes to the returned inner [Maybe].
 * Emits the value from the inner [Maybe].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#flatMap-io.reactivex.functions.Function-).
 */
fun <T, R> Maybe<T>.flatMap(mapper: (T) -> Maybe<R>): Maybe<R> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch {
                        mapper(value).subscribe(
                            object : MaybeObserver<R>, Observer by this, MaybeCallbacks<R> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * Calls the [mapper] with the value emitted by the [Maybe] and subscribes to the returned inner [Maybe].
 * When the inner [Maybe] emits, calls the [resultSelector] function with the original and the inner values and emits the result.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#flatMap-io.reactivex.functions.Function-).
 */
fun <T, U, R> Maybe<T>.flatMap(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Maybe<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
