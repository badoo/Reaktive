package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(arrayListOf()) { list, item -> list.add(item) }