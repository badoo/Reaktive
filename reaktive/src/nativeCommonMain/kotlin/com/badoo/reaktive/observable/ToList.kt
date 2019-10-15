package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import kotlin.native.concurrent.isFrozen

actual fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(ArrayList()) { list, item ->
        val newList =
            if (list.isFrozen) {
                // We have to create a new ArrayList
                ArrayList<T>(list.size + 1)
                    .apply { addAll(list) }
            } else {
                list
            }

        newList.add(item)
        newList
    }
