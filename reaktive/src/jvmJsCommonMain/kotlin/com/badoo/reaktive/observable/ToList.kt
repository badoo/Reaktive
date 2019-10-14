package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

actual fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(ArrayList()) { list, item ->
        list.add(item)
        list
    }
