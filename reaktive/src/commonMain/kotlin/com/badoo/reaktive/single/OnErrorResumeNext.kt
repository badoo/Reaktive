package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Single] signals `onError`, resumes the flow with a new [Single] returned by [nextSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#onErrorResumeNext-io.reactivex.functions.Function-).
 */
fun <T> Single<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Single<T>): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch({ nextSupplier(error) }, { CompositeException(error, it) }) {
                        it.subscribeSafe(
                            object : SingleObserver<T>, Observer by this, SingleCallbacks<T> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * When the [Single] signals `onError`, resumes the flow with [next] [Single].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#onErrorResumeNext-io.reactivex.Single-).
 */
fun <T> Single<T>.onErrorResumeNext(next: Single<T>): Single<T> =
    onErrorResumeNext { next }
