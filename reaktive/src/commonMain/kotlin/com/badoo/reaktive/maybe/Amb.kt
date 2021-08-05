package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean

/**
 * Runs multiple [Maybe]s and signals the events of the first one to terminate (disposing the rest).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#amb-java.lang.Iterable-).
 */
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

/**
 * Runs multiple [Maybe]s and signals the events of the first one to terminate (disposing the rest).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#ambArray-io.reactivex.MaybeSource...-).
 */
fun <T> amb(vararg sources: Maybe<T>): Maybe<T> = sources.asList().amb()
