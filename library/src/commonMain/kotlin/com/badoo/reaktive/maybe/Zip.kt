package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T, R> Collection<Maybe<T>>.zip(mapper: (List<T>) -> R): Maybe<R> =
    maybeByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val lock = newLock()
        val values = MutableList<T?>(size) { null }
        var valueCounter = size

        forEachIndexed { index, source ->
            source.subscribeSafe(
                object : MaybeObserver<T> {

                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }

                    override fun onSuccess(value: T) {
                        lock
                            .synchronized {
                                values[index] = value
                                --valueCounter
                            }
                            .takeIf { it == 0 }
                            ?.let {
                                @Suppress("UNCHECKED_CAST")
                                values as List<T>
                            }
                            ?.let {
                                try {
                                    mapper(it)
                                } catch (e: Throwable) {
                                    emitter.onError(e)
                                    return
                                }
                            }
                            ?.also(emitter::onSuccess)
                    }

                    override fun onComplete() {
                        lock.synchronized(emitter::onComplete)
                    }

                    override fun onError(error: Throwable) {
                        lock.synchronized {
                            emitter.onError(error)
                        }
                    }
                }
            )
        }
    }

fun <T, R> zip(vararg sources: Maybe<T>, mapper: (List<T>) -> R): Maybe<R> =
    sources.toList().zip(mapper)

fun <T1, T2, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    mapper: (T1, T2) -> R
): Maybe<R> =
    listOf(source1, source2)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2)
        }

fun <T1, T2, T3, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    mapper: (T1, T2, T3) -> R
): Maybe<R> =
    listOf(source1, source2, source3)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3)
        }

fun <T1, T2, T3, T4, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    mapper: (T1, T2, T3, T4) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4)
        }

fun <T1, T2, T3, T4, T5, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    mapper: (T1, T2, T3, T4, T5) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5)
        }

fun <T1, T2, T3, T4, T5, T6, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    source6: Maybe<T6>,
    mapper: (T1, T2, T3, T4, T5, T6) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5, source6)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5, values[5] as T6)
        }

fun <T1, T2, T3, T4, T5, T6, T7, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    source6: Maybe<T6>,
    source7: Maybe<T7>,
    mapper: (T1, T2, T3, T4, T5, T6, T7) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6,
                values[6] as T7
            )
        }

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    source6: Maybe<T6>,
    source7: Maybe<T7>,
    source8: Maybe<T8>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6,
                values[6] as T7,
                values[7] as T8
            )
        }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    source6: Maybe<T6>,
    source7: Maybe<T7>,
    source8: Maybe<T8>,
    source9: Maybe<T9>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8, source9)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6,
                values[6] as T7,
                values[7] as T8,
                values[8] as T9
            )
        }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> zip(
    source1: Maybe<T1>,
    source2: Maybe<T2>,
    source3: Maybe<T3>,
    source4: Maybe<T4>,
    source5: Maybe<T5>,
    source6: Maybe<T6>,
    source7: Maybe<T7>,
    source8: Maybe<T8>,
    source9: Maybe<T9>,
    source10: Maybe<T10>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Maybe<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8, source9, source10)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6,
                values[6] as T7,
                values[7] as T8,
                values[8] as T9,
                values[9] as T10
            )
        }