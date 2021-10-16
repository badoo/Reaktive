package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * When the [Observable] signals `onError`, resumes the flow with a new [Observable] returned by [nextSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#onErrorResumeNext-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Observable<T>): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch({ nextSupplier(error) }, { CompositeException(error, it) }) {
                        it.subscribeSafe(
                            object : ObservableObserver<T>, Observer by this, ObservableCallbacks<T> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

/**
 * When the [Observable] signals `onError`, resumes the flow with [next][next] [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#onErrorResumeNext-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.onErrorResumeNext(next: Observable<T>): Observable<T> =
    onErrorResumeNext { next }
