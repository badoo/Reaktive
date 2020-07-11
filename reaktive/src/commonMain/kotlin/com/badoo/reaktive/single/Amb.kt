package com.badoo.reaktive.single

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Iterable<Single<T>>.amb(): Single<T> =
    com.badoo.reaktive.single.single { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onError(NoSuchElementException())
            return@single
        }

        val disposableObserver =
            object : CompositeDisposableObserver(), SingleObserver<T> {
                private val isFinished = AtomicBoolean()

                override fun onSuccess(value: T) {
                    race { emitter.onSuccess(value) }
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

fun <T> amb(vararg sources: Single<T>): Single<T> = sources.asList().amb()
