package com.badoo.reaktive.maybe

/**
 * Creates a [Maybe] with manual signalling via [MaybeEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#create-io.reactivex.MaybeOnSubscribe-).
 */
expect fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T>
