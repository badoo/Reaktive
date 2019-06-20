package com.badoo.reaktive.completable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.getAndUpdate

fun Iterable<Completable>.concat(): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val index = AtomicReference(0)
        val list = toList()

        if (list.isEmpty()) {
            observer.onComplete()
            return@completableUnsafe
        }

        val upstreamObserver =
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    val next = list.getOrNull(index.getAndUpdate(Int::inc))
                    if (next != null) {
                        next.subscribeSafe(this)
                    } else {
                        observer.onComplete()
                    }
                }
            }

        list[index.getAndUpdate(Int::inc)].subscribe(upstreamObserver)
    }

fun concat(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .concat()