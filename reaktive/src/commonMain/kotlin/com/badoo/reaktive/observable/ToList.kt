package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(emptyList()) { list, item -> list + item }
    