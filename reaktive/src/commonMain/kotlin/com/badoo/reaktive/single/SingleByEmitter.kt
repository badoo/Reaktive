package com.badoo.reaktive.single

/**
 * Creates a [Single] with manual signalling via [SingleEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#create-io.reactivex.SingleOnSubscribe-).
 */
expect fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T>
