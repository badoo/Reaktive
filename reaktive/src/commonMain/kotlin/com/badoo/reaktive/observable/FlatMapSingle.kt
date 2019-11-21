package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.map
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    observable { emitter ->
        val serializedEmitter = emitter.serialize()

        val upstreamObserver =
            object : CompositeDisposableObserver(), ObservableObserver<T>, ErrorCallback by serializedEmitter {
                private val activeSourceCount = AtomicInt(1)

                private val mappedObserver: SingleObserver<R> =
                    object : SingleObserver<R>, Observer by this, ErrorCallback by serializedEmitter {
                        override fun onSuccess(value: R) {
                            serializedEmitter.onNext(value)
                            onComplete()
                        }
                    }

                override fun onNext(value: T) {
                    activeSourceCount.addAndGet(1)
                    serializedEmitter.tryCatch(block = { mapper(value).subscribe(mappedObserver) })
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

fun <T, U, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapSingle { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
