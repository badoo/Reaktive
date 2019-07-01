package com.badoo.reaktive.completable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicInt

fun Iterable<Completable>.concat(): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val sources = toList()

        if (sources.isEmpty()) {
            observer.onComplete()
            return@completableUnsafe
        }

        val sourceIndex = AtomicInt()

        val upstreamObserver =
            object : CompletableObserver, CompletableCallbacks by observer {
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

fun concat(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .concat()