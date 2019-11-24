package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.serializer.serializer

fun <T> Observable<T>.repeat(count: Int = -1): Observable<T> =
    observable { emitter ->
        val observer =
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
                private val counter: AtomicInt? = if (count >= 0) AtomicInt(count) else null

                // Prevents recursive subscriptions
                private val serializer =
                    serializer<Unit> {
                        subscribe(this)
                        true
                    }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    if ((counter == null) || (counter.addAndGet(-1) >= 0)) {
                        if (!emitter.isDisposed) {
                            subscribeToUpstream()
                        }
                    } else {
                        emitter.onComplete()
                    }
                }

                fun subscribeToUpstream() {
                    serializer.accept(Unit)
                }
            }

        observer.subscribeToUpstream()
    }
