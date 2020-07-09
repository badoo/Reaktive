package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Iterable<Observable<T>>.concat(): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val sources = toList()

        if (sources.isEmpty()) {
            observer.onComplete()
            return@observableUnsafe
        }

        val sourceIndex = AtomicInt()

        val upstreamObserver =
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    sourceIndex
                        .addAndGet(1)
                        .let(sources::getOrNull)
                        ?.subscribeSafe(this)
                        ?: observer.onComplete()
                }
            }

        sources[0].subscribeSafe(upstreamObserver)
    }

fun <T> concat(vararg sources: Observable<T>): Observable<T> =
    sources
        .asList()
        .concat()
