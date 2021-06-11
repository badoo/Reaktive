package com.badoo.reaktive.completable

/**
 * Creates a [Completable] with manual signalling via [CompletableEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#create-io.reactivex.CompletableOnSubscribe-).
 */
expect fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable
