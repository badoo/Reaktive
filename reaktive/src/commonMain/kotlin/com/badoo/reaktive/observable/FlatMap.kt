package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T, R> Observable<T>.flatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val serializedEmitter = emitter.serialize()

        val upstreamObserver =
            object : CompositeDisposableObserver(), ObservableObserver<T>, ErrorCallback by serializedEmitter {
                private val activeSourceCount = AtomicInt(1)

                private val mappedObserver =
                    object : ObservableObserver<R>, Observer by this, CompletableCallbacks by this,
                        ValueCallback<R> by serializedEmitter {
                    }

                override fun onNext(value: T) {
                    activeSourceCount.addAndGet(1)
                    serializedEmitter.tryCatch { mapper(value).subscribe(mappedObserver) }
                }

                override fun onComplete() {
                    if (activeSourceCount.addAndGet(-1) <= 0) {
                        serializedEmitter.onComplete()
                    }
                }
            }

        emitter.setDisposable(upstreamObserver)

        subscribe(upstreamObserver)
    }

fun <T, U, R> Observable<T>.flatMap(mapper: (T) -> Observable<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
