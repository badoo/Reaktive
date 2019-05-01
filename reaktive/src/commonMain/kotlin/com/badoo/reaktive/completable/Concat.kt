package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun Iterable<Completable>.concat(): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val iterator = iterator()
        if (!iterator.hasNext()) {
            observer.onComplete()
            return@completableUnsafe
        }

        val upstreamObserver =
            object : CompletableObserver, CompletableCallbacks by observer {
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

fun concat(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .concat()