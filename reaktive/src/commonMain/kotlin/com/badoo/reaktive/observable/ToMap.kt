package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

fun <T, K> Observable<T>.toMap(keySelector: (T) -> K): Single<Map<K, T>> =
    toMap(keySelector) { it }

fun <T, K, V> Observable<T>.toMap(keySelector: (T) -> K, valueSelector: (T) -> V): Single<Map<K, V>> =
    collect(emptyMap()) { map, item ->
        val key = keySelector(item)
        val value = valueSelector(item)
        return@collect map + mapOf(key to value)
    }