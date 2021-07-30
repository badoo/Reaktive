package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeatWhen

/**
 * When the [Maybe] signals `onSuccess` or `onComplete`,
 * re-subscribes to the [Maybe] when the [Maybe] returned by the [handler] function emits a value.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#repeatWhen-io.reactivex.functions.Function-).
 */
fun <T> Maybe<T>.repeatWhen(handler: (repeatNumber: Int) -> Maybe<*>): Observable<T> =
    asObservable().repeatWhen(handler)
