package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Observable<T>.takeUntil(predicate: (T) -> Boolean): Observable<T> =
    observable { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onNext(value)

                    emitter.tryCatch(block = { predicate(value) }) {
                        if (it) {
                            emitter.onComplete()
                        }
                    }
                }
            }
        )
    }
