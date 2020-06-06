package com.badoo.reaktive.completable

expect fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable
