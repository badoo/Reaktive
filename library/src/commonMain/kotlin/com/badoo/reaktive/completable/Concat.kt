package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun Iterable<Completable>.concat(): Completable =
    completable { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val iterator = iterator()
        if (!iterator.hasNext()) {
            observer.onComplete()
            return@completable
        }

        val upstreamObserver =
            object : CompletableObserver by observer {
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