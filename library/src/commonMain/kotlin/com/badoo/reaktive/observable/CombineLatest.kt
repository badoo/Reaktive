package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T, R> Collection<Observable<T>>.combineLatest(mapper: (List<T>) -> R): Observable<R> =
    observableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val lock = newLock()
        val dummy = Any()
        val values = MutableList<Any?>(size) { dummy }
        var readyValues: List<T>? = null
        var activeSourceCount = size

        forEachIndexed { index, source ->
            source.subscribeSafe(
                object : ObservableObserver<T> {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }

                    override fun onNext(value: T) {
                        lock.synchronized {
                            values[index] = value
                            if ((readyValues == null) && values.none { it === dummy }) {
                                @Suppress("UNCHECKED_CAST")
                                readyValues = values as List<T>
                            }

                            readyValues
                                ?.let(mapper)
                                ?.also(emitter::onNext)
                        }
                    }

                    override fun onComplete() {
                        lock.synchronized {
                            activeSourceCount--
                            if (activeSourceCount == 0) {
                                emitter.onComplete()
                            }
                        }
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

fun <T, R> combineLatest(vararg sources: Observable<T>, mapper: (List<T>) -> R): Observable<R> =
    sources.toList().combineLatest(mapper)

fun <T1, T2, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    mapper: (T1, T2) -> R
): Observable<R> =
    listOf(source1, source2)
        .combineLatest { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2)
        }

fun <T1, T2, T3, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    mapper: (T1, T2, T3) -> R
): Observable<R> =
    listOf(source1, source2, source3)
        .combineLatest { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3)
        }

fun <T1, T2, T3, T4, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    mapper: (T1, T2, T3, T4) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4)
        .combineLatest { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4)
        }

fun <T1, T2, T3, T4, T5, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    mapper: (T1, T2, T3, T4, T5) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5)
        .combineLatest { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5)
        }

fun <T1, T2, T3, T4, T5, T6, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    mapper: (T1, T2, T3, T4, T5, T6) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5, source6)
        .combineLatest { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5, values[5] as T6)
        }

fun <T1, T2, T3, T4, T5, T6, T7, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    mapper: (T1, T2, T3, T4, T5, T6, T7) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7)
        .combineLatest { values ->
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

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8)
        .combineLatest { values ->
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

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>,
    source9: Observable<T9>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8, source9)
        .combineLatest { values ->
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

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>,
    source9: Observable<T9>,
    source10: Observable<T10>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Observable<R> =
    listOf(source1, source2, source3, source4, source5, source6, source7, source8, source9, source10)
        .combineLatest { values ->
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