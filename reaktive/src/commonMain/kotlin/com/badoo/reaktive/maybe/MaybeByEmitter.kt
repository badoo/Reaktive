package com.badoo.reaktive.maybe

expect fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T>
