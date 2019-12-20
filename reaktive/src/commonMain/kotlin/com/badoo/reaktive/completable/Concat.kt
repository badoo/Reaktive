package com.badoo.reaktive.completable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicInt

fun Iterable<Completable>.concat(): Completable =
    completable { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onComplete()
            return@completable
        }

        val sourceIndex = AtomicInt()

        val upstreamObserver =
            object : CompletableObserver, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    sourceIndex
                        .addAndGet(1)
                        .let(sources::getOrNull)
                        ?.subscribeSafe(this)
                        ?: emitter.onComplete()
                }
            }

        sources[0].subscribe(upstreamObserver)
    }

fun concat(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .concat()
