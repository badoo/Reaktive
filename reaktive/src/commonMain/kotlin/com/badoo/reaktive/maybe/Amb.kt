package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Iterable<Maybe<T>>.amb(): Maybe<T> =
    maybe { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onComplete()
            return@maybe
        }

        val disposableObserver =
            object : CompositeDisposableObserver(), MaybeObserver<T> {
                private val isFinished = AtomicBoolean()

                override fun onSuccess(value: T) {
                    race { emitter.onSuccess(value) }
                }

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

fun <T> amb(vararg sources: Maybe<T>): Maybe<T> = sources.asList().amb()
