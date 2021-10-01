package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that emits only elements of type [T] from the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#ofType-java.lang.Class-).
 */
inline fun <reified T> Observable<*>.ofType(): Observable<T> =
    filter { it is T }
        .map { it as T }
