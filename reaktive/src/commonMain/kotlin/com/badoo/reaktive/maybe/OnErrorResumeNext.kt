package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Maybe] signals `onError`, resumes the flow with a new [Maybe] returned by [nextSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#onErrorResumeNext-io.reactivex.functions.Function-).
 */
fun <T> Maybe<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Maybe<T>): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch({ nextSupplier(error) }, { CompositeException(error, it) }) {
                        it.subscribeSafe(
                            object : MaybeObserver<T>, Observer by this, MaybeCallbacks<T> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * When the [Maybe] signals `onError`, resumes the flow with [next][next] [Maybe].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#onErrorResumeNext-io.reactivex.MaybeSource-).
 */
fun <T> Maybe<T>.onErrorResumeNext(next: Maybe<T>): Maybe<T> =
    onErrorResumeNext { next }
