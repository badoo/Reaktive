package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable

/**
 * Returns an [Observable] that applies the [mapper] to every element emitted by the source [Observable]
 * and concatenates the returned [Maybe]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.concatMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    concatMap {
        mapper(it).asObservable()
    }
