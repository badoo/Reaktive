package com.badoo.reaktive.completable

inline fun completableUnsafe(crossinline onSubscribe: (observer: CompletableObserver) -> Unit): Completable =
    object : Completable {
        override fun subscribe(observer: CompletableObserver) {
            onSubscribe(observer)
        }
    }

fun completableOfError(error: Throwable): Completable =
    completable { emitter ->
        emitter.onError(error)
    }

fun Throwable.toCompletableOfError(): Completable = completableOfError(this)

fun completableOfEmpty(): Completable = completable(CompletableEmitter::onComplete)

fun completableOfNever(): Completable = completable {}

fun completableFromFunction(func: () -> Unit): Completable =
    completable { emitter ->
        func()
        emitter.onComplete()
    }