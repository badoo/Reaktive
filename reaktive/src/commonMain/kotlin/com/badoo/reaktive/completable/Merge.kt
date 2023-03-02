package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicInt

/**
 * Merges multiple [Completable]s into one [Completable], running all [Completable]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#merge-java.lang.Iterable-).
 */
fun Iterable<Completable>.merge(): Completable =
    completable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()
        val activeSourceCount = AtomicInt(1)

        forEach { upstream ->
            activeSourceCount.addAndGet(1)
            upstream.subscribe(
                object : CompletableObserver, ErrorCallback by serializedEmitter {
                    private var disposableRef: Disposable? = null

                    override fun onSubscribe(disposable: Disposable) {
                        disposableRef = disposable
                        disposables += disposable
                    }

                    override fun onComplete() {
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

/**
 * Merges multiple [Completable]s into one [Completable], running all [Completable]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#mergeArray-io.reactivex.CompletableSource...-).
 */
fun merge(vararg sources: Completable): Completable =
    sources
        .asList()
        .merge()
