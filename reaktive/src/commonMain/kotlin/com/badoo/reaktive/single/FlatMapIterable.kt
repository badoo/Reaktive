package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable

/**
 * Calls the [transformer] with the value emitted by the [Single] and emits the returned [Iterable] values one by one as [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMapObservable-io.reactivex.functions.Function-).
 */
fun <T, R> Single<T>.flatMapIterable(transformer: (T) -> Iterable<R>): Observable<R> =
    flatMapObservable { transformer(it).asObservable() }
