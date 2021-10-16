@file:Suppress("MagicNumber")

package com.badoo.reaktive.single

import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.zip

/**
 * Subscribes to all provided [Single]s, accumulates all their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-java.lang.Iterable-io.reactivex.functions.Function-).
 */
fun <T, R> Iterable<Single<T>>.zip(mapper: (List<T>) -> R): Single<R> =
    map(Single<T>::asObservable)
        .zip(mapper)
        .firstOrError()

/**
 * Subscribes to all [sources] [Single]s, accumulates all their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zipArray-io.reactivex.functions.Function-io.reactivex.SingleSource...-).
 */
fun <T, R> zip(vararg sources: Single<T>, mapper: (List<T>) -> R): Single<R> =
    sources.toList().zip(mapper)

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.BiFunction-).
 */
fun <T1, T2, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    mapper: (T1, T2) -> R
): Single<R> =
    listOf(source1, source2)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2)
        }

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function3-).
 */
fun <T1, T2, T3, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    mapper: (T1, T2, T3) -> R
): Single<R> =
    listOf(source1, source2, source3)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3)
        }

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function4-).
 */
fun <T1, T2, T3, T4, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    mapper: (T1, T2, T3, T4) -> R
): Single<R> =
    listOf(source1, source2, source3, source4)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4)
        }

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function5-).
 */
fun <T1, T2, T3, T4, T5, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    mapper: (T1, T2, T3, T4, T5) -> R
): Single<R> =
    listOf(source1, source2, source3, source4, source5)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5)
        }

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function6-).
 */
fun <T1, T2, T3, T4, T5, T6, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    source6: Single<T6>,
    mapper: (T1, T2, T3, T4, T5, T6) -> R
): Single<R> =
    listOf(source1, source2, source3, source4, source5, source6)
        .zip { values ->
            @Suppress("UNCHECKED_CAST")
            mapper(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4, values[4] as T5, values[5] as T6)
        }

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function7-).
 */
fun <T1, T2, T3, T4, T5, T6, T7, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    source6: Single<T6>,
    source7: Single<T7>,
    mapper: (T1, T2, T3, T4, T5, T6, T7) -> R
): Single<R> =
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

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function8-).
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    source6: Single<T6>,
    source7: Single<T7>,
    source8: Single<T8>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Single<R> =
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

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function9-).
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    source6: Single<T6>,
    source7: Single<T7>,
    source8: Single<T8>,
    source9: Single<T9>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Single<R> =
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

/**
 * Subscribes to all `source` [Single]s, accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zip-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.SingleSource-io.reactivex.functions.Function9-).
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> zip(
    source1: Single<T1>,
    source2: Single<T2>,
    source3: Single<T3>,
    source4: Single<T4>,
    source5: Single<T5>,
    source6: Single<T6>,
    source7: Single<T7>,
    source8: Single<T8>,
    source9: Single<T9>,
    source10: Single<T10>,
    mapper: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Single<R> =
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
