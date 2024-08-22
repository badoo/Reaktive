package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.retryWhen

/**
 * Returns a [Single] that automatically resubscribes to this [Single] if it signals `onError`
 * and the [Observable] returned by the [handler] function emits a value for that specific [Throwable].
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#retryWhen-io.reactivex.functions.Function-).
 */
fun <T> Single<T>.retryWhen(handler: (Observable<Throwable>) -> Observable<*>): Single<T> =
    asObservable()
        .retryWhen(handler)
        .firstOrError()
