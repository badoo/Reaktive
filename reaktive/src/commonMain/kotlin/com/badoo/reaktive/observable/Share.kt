package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that shares a single subscription to the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#share--).
 */
fun <T> Observable<T>.share(): Observable<T> = publish().refCount()
