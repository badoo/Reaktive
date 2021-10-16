package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.single

/**
 * Returns a [Maybe] that first subscribes to this [Maybe] and signals its events, unless this [Maybe] signals `onComplete`.
 * If this [Maybe] signals `onComplete`, then subscribes to the [other] [Maybe] and signals its events.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#switchIfEmpty-io.reactivex.MaybeSource-).
 */
fun <T> Maybe<T>.switchIfEmpty(other: Maybe<T>): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    other.subscribeSafe(
                        object : MaybeObserver<T>, Observer by this, MaybeCallbacks<T> by emitter {
                        }
                    )
                }
            }
        )
    }

/**
 * Returns a [Maybe] that first subscribes to this [Maybe] and signals its events, unless this [Maybe] signals `onComplete`.
 * If this [Maybe] signals `onComplete`, then subscribes to the [other] [Single] and signals its events.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#switchIfEmpty-io.reactivex.SingleSource-).
 */
fun <T> Maybe<T>.switchIfEmpty(other: Single<T>): Single<T> =
    single { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    other.subscribeSafe(
                        object : SingleObserver<T>, Observer by this, SingleCallbacks<T> by emitter {
                        }
                    )
                }
            }
        )
    }
