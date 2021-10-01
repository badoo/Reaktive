package com.badoo.reaktive.observable

/**
 * Creates an [Observable] with manual signalling via [ObservableEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#create-io.reactivex.ObservableOnSubscribe-).
 */
expect fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T>
