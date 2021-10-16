package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Single],
 * disposing any previously subscribed inner [Single]. Emits elements from inner [Single]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMapSingle-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.switchMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    switchMap {
        mapper(it).asObservable()
    }

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Single],
 * disposing any previously subscribed inner [Single]. Emits elements from inner [Single]s.
 * For an element [U] emitted by an inner [Single], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMapSingle-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.switchMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    switchMapSingle { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
