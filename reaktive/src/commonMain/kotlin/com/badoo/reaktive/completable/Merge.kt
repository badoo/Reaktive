package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.atomic.AtomicInt

fun Iterable<Completable>.merge(): Completable =
    completable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()
        val activeSourceCount = AtomicInt(1)

        forEach { upstream ->
            activeSourceCount.addAndGet(1)
            upstream.subscribe(
                object : ObjectReference<Disposable?>(null), CompletableObserver, ErrorCallback by serializedEmitter {
                    override fun onSubscribe(disposable: Disposable) {
                        value = disposable
                        disposables += disposable
                    }

                    override fun onComplete() {
                        disposables -= requireNotNull(value)
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

fun merge(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .merge()
