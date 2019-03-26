package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Subscribable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.serializer.serializer

fun <T, R> Observable<T>.flatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        subscribeSafe(
            object : ObservableObserver<T> {
                private val lock = newLock()
                private var activeSourceCount = 1

                private val serializer =
                    serializer<Any?> {
                        if (it is FlatMapEvent) {
                            when (it) {
                                is FlatMapEvent.OnComplete -> emitter.onComplete()
                                is FlatMapEvent.OnError -> emitter.onError(it.error)
                            }
                        } else {
                            @Suppress("UNCHECKED_CAST") // Either FlatMapEvent or R, to avoid unnecessary allocations
                            emitter.onNext(it as R)
                        }

                        true
                    }

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    lock.synchronized {
                        activeSourceCount++
                    }

                    try {
                        mapper(value)
                    } catch (e: Throwable) {
                        onError(e)
                        return
                    }
                        .subscribeSafe(
                            object : ObservableObserver<R>, Subscribable by this, CompletableCallbacks by this {
                                override fun onNext(value: R) {
                                    serializer.accept(value)
                                }
                            }
                        )
                }

                override fun onComplete() {
                    lock.synchronized {
                        activeSourceCount--
                        if (activeSourceCount > 0) {
                            return
                        }
                    }

                    serializer.accept(FlatMapEvent.OnComplete)
                }

                override fun onError(error: Throwable) {
                    serializer.accept(FlatMapEvent.OnError(error))
                }
            }
        )
    }

private sealed class FlatMapEvent {

    object OnComplete : FlatMapEvent()
    class OnError(val error: Throwable) : FlatMapEvent()
}