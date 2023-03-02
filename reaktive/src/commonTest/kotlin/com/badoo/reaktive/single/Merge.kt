package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.serialize
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Iterable<Single<T>>.merge(): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()
        val activeSourceCount = AtomicInt(1)

        forEach { upstream ->
            activeSourceCount.addAndGet(1)
            upstream.subscribe(
                object : SingleObserver<T>, ErrorCallback by serializedEmitter {
                    private var disposableRef: Disposable? = null

                    override fun onSubscribe(disposable: Disposable) {
                        disposableRef = disposable
                        disposables += disposable
                    }

                    override fun onSuccess(value: T) {
                        serializedEmitter.onNext(value)

                        disposables -= requireNotNull(disposableRef)
                        if (activeSourceCount.addAndGet(-1) == 0) {
                            emitter.onComplete()
                        }
                    }
                }
            )
        }

        if (activeSourceCount.addAndGet(-1) == 0) {
            emitter.onComplete()
        }
    }

fun <T> merge(vararg sources: Single<T>): Observable<T> =
    sources
        .asIterable()
        .merge()
