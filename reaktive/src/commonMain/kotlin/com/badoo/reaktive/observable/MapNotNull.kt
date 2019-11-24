package com.badoo.reaktive.observable

fun <T, R : Any> Observable<T>.mapNotNull(mapper: (T) -> R?): Observable<R> = map(mapper).notNull()
