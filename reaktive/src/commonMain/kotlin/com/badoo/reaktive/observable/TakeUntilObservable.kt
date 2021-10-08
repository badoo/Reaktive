package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign

/**
 * Returns an [Observable] that emits elements emitted by the source [Observable] until the [other][other] [Observable] emits an element.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#takeUntil-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.takeUntil(other: Observable<*>): Observable<T> =
    observable {
        val emitter = it.serialize()
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val upstreamObserver =
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }
            }

        other.subscribe(
            object : ObservableObserver<Any?>, Observer by upstreamObserver, CompletableCallbacks by upstreamObserver {
                override fun onNext(value: Any?) {
                    upstreamObserver.onComplete()
                }
            }
        )

        subscribe(upstreamObserver)
    }
