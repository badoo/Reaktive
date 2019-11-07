package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.isFrozen

fun <T, K> Observable<T>.toMap(keySelector: (T) -> K): Single<Map<K, T>> =
    toMap(keySelector) { it }

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
