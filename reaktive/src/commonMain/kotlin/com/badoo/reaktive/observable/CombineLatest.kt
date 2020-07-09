@file:Suppress("MagicNumber")

package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.serializer.serializer

fun <T, R> Iterable<Observable<T>>.combineLatest(mapper: (List<T>) -> R): Observable<R> =
    observable { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onComplete()
            return@observable
        }

        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val values = SharedList<Any?>(sources.size) { Uninitialized }
        val activeSourceCount = AtomicInt(sources.size)

        val serializer =
            serializer<CombineLatestEvent<T>> { event ->
                when (event) {
                    is CombineLatestEvent.OnNext -> {
                        values[event.index] = event.value

                        if (values.none { it === Uninitialized }) {
                            val mappedValue =
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    mapper(values as List<T>)
                                } catch (e: Throwable) {
                                    emitter.onError(e)
                                    return@serializer false
                                }

                            emitter.onNext(mappedValue)
                        }

                        true
                    }

                    is CombineLatestEvent.OnComplete -> {
                        val remainingActiveSources = activeSourceCount.addAndGet(-1)

                        // Complete if all sources are completed or a source is completed without a value
                        val allCompleted = (remainingActiveSources == 0) || (values[event.index] === Uninitialized)
                        if (allCompleted) {
                            emitter.onComplete()
                        }

                        !allCompleted
                    }

                    is CombineLatestEvent.OnError -> {
                        emitter.onError(event.error)
                        false
                    }
                }
            }

        sources.forEachIndexed { index, source ->
            source.subscribe(
                object : ObservableObserver<T> {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }

                    override fun onNext(value: T) {
                        serializer.accept(CombineLatestEvent.OnNext(index, value))
                    }

                    override fun onComplete() {
                        serializer.accept(CombineLatestEvent.OnComplete(index))
                    }

                    override fun onError(error: Throwable) {
                        serializer.accept(CombineLatestEvent.OnError(error))
                    }
                }
            )
        }
    }

private sealed class CombineLatestEvent<out T> {

    class OnNext<out T>(val index: Int, val value: T) : CombineLatestEvent<T>()
    class OnComplete(val index: Int) : CombineLatestEvent<Nothing>()
    class OnError(val error: Throwable) : CombineLatestEvent<Nothing>()
}

fun <T, R> combineLatest(vararg sources: Observable<T>, mapper: (List<T>) -> R): Observable<R> =
    sources
        .asList()
        .combineLatest(mapper)

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
