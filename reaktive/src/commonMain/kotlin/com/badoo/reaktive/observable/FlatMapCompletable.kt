package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.completable.serialize
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Observable<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    completable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()

        subscribeSafe(
            object : ObservableObserver<T>, ErrorCallback by serializedEmitter {
                private val activeSourceCount = AtomicInt(1)

                private val mappedObserver: CompletableObserver =
                    object : CompletableObserver, Observer by this, CompletableCallbacks by this {
                    }

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
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
        )
    }
