package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.retryWhen

/**
 * Returns a [Maybe] that automatically resubscribes to this [Maybe] if it signals `onError`
 * and the [Observable] returned by the [handler] function emits a value for that specific [Throwable].
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#retryWhen-io.reactivex.functions.Function-).
 */
fun <T> Maybe<T>.retryWhen(handler: (Observable<Throwable>) -> Observable<*>): Maybe<T> =
    asObservable()
        .retryWhen(handler)
        .firstOrComplete()
