package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun Iterable<Completable>.amb(): Completable =
    completable { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onComplete()
            return@completable
        }

        val disposableObserver =
            object : CompositeDisposableObserver(), CompletableObserver {
                private val isFinished = AtomicBoolean()

                override fun onComplete() {
                    race(emitter::onComplete)
                }

                override fun onError(error: Throwable) {
                    race { emitter.onError(error) }
                }

                private inline fun race(block: () -> Unit) {
                    if (isFinished.compareAndSet(false, true)) {
                        block()
                    }
                }
            }

        emitter.setDisposable(disposableObserver)

        sources.forEach { it.subscribe(disposableObserver) }
    }

fun amb(vararg sources: Completable): Completable = sources.asList().amb()
