package com.badoo.reaktive.single

expect fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T>
