package com.badoo.reaktive.observable

/**
 * A shortcut for [map] followed by [notNull]
 */
fun <T, R : Any> Observable<T>.mapNotNull(mapper: (T) -> R?): Observable<R> = map(mapper).notNull()
