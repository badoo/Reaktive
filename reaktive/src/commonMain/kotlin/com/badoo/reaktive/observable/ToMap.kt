package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.isFrozen

/**
 * Returns a [Single] that emits a [Map] containing all elements emitted by the **finite** source [Observable],
 * associated with keys returned by [keySelector].
 *
 * ⚠️ Warning: if the emitted [Map] becomes [frozen](https://github.com/badoo/Reaktive#kotlin-native-pitfalls)
 * somewhere in the downstream in Kotlin/Native, then the operator will copy the [Map] on next iteration,
 * which can significantly affect performance.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#toMap-io.reactivex.functions.Function-).
 */
fun <T, K> Observable<T>.toMap(keySelector: (T) -> K): Single<Map<K, T>> =
    toMap(keySelector) { it }

/**
 * Returns a [Single] that emits a [Map] containing all elements emitted by the **finite** source [Observable],
 * transformed by [valueSelector] and associated with keys returned by [keySelector].
 *
 * ⚠️ Warning: if the emitted [Map] becomes [frozen](https://github.com/badoo/Reaktive#kotlin-native-pitfalls)
 * somewhere in the downstream in Kotlin/Native, then the operator will copy the [Map] on next iteration,
 * which can significantly affect performance.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#toMap-io.reactivex.functions.Function-io.reactivex.functions.Function-).
 */
fun <T, K, V> Observable<T>.toMap(keySelector: (T) -> K, valueSelector: (T) -> V): Single<Map<K, V>> =
    collect(LinkedHashMap()) { map, item ->
        val key = keySelector(item)
        val value = valueSelector(item)
        if (map.isFrozen) {
            LinkedHashMap<K, V>(map.size + 1).apply {
                putAll(map)
                put(key, value)
            }
        } else {
            map[key] = value
            map
        }
    }
