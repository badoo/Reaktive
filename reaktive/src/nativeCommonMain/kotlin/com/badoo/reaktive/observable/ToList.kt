package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import kotlin.native.concurrent.isFrozen

actual fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(ArrayList()) { list, item ->
        val actualList = list.takeUnless(Any::isFrozen) ?: ArrayList<T>(list.size + 1).apply { addAll(list) }
        actualList += item
        actualList
    }
