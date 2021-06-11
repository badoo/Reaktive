package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Completable] signals `onError`, resumes the flow with a new [Completable] returned by [nextSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#onErrorResumeNext-io.reactivex.functions.Function-).
 */
fun Completable.onErrorResumeNext(nextSupplier: (Throwable) -> Completable): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch(
                        { nextSupplier(error) },
                        { CompositeException(error, it) }
                    ) {
                        it.subscribeSafe(
                            object : CompletableObserver, Observer by this, CompletableCallbacks by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * When the [Completable] signals `onError`, resumes the flow with `next` [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#onErrorResumeNext-io.reactivex.functions.Function-).
 */
fun Completable.onErrorResumeNext(next: Completable): Completable =
    onErrorResumeNext { next }
