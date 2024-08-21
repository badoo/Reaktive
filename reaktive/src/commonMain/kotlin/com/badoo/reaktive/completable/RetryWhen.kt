package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.retryWhen

/**
 * Returns a [Completable] that automatically resubscribes to this [Completable] if it signals `onError`
 * and the [Observable] returned by the [handler] function emits a value for that specific [Throwable].
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#retryWhen-io.reactivex.functions.Function-).
 */
fun Completable.retryWhen(handler: (Observable<Throwable>) -> Observable<*>): Completable =
    asObservable()
        .retryWhen(handler)
        .asCompletable()
