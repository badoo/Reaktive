package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asObservable

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Completable],
 * disposing any previously subscribed inner [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMapCompletable-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.switchMapCompletable(mapper: (T) -> Completable): Completable =
    switchMap {
        mapper(it).asObservable<Nothing>()
    }
        .asCompletable()
