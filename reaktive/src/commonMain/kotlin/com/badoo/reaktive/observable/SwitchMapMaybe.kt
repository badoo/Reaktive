package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Maybe],
 * disposing any previously subscribed inner [Maybe]. Emits elements from inner [Maybe]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.switchMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    switchMap {
        mapper(it).asObservable()
    }

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Maybe],
 * disposing any previously subscribed inner [Maybe]. Emits elements from inner [Maybe]s.
 * For an element [U] emitted by an inner [Maybe], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.switchMapMaybe(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Observable<R> =
    switchMapMaybe { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
