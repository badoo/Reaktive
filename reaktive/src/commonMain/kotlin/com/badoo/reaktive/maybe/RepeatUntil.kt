package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeatUntil

/**
 * When the [Maybe] signals `onSuccess` or `onComplete`, re-subscribes to the [Maybe] if the [predicate] function returns `false`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#repeatUntil-io.reactivex.functions.BooleanSupplier-).
 */
fun <T> Maybe<T>.repeatUntil(predicate: () -> Boolean): Observable<T> = asObservable().repeatUntil(predicate)
