package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable

/**
 * Returns an [Observable] that applies the [mapper] to every element emitted by the source [Observable]
 * and concatenates the returned [Single]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concatMapSingle-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.concatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    concatMap {
        mapper(it).asObservable()
    }
