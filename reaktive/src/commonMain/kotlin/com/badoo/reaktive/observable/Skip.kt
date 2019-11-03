package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicLong

fun <T> Observable<T>.skip(count: Long): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                private var remaining = AtomicLong(count)

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    if (remaining.value != 0L) {
                        remaining.addAndGet(-1)
                    } else {
                        emitter.onNext(value)
                    }
                }
            }
        )
    }
