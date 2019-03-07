package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Iterable<Observable<T>>.concat(): Observable<T> =
    observable { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val iterator = iterator()
        if (!iterator.hasNext()) {
            observer.onComplete()
            return@observable
        }

        val upstreamObserver =
            object : ObservableObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    if (iterator.hasNext()) {
                        iterator
                            .next()
                            .subscribeSafe(this)
                    } else {
                        observer.onComplete()
                    }
                }
            }

        iterator
            .next()
            .subscribeSafe(upstreamObserver)
    }

fun <T> concat(vararg sources: Observable<T>): Observable<T> =
    sources
        .asIterable()
        .concat()