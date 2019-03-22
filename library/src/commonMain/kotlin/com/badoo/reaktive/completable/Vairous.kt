package com.badoo.reaktive.completable

inline fun completable(crossinline onSubscribe: (observer: CompletableObserver) -> Unit): Completable =
    object : Completable {
        override fun subscribe(observer: CompletableObserver) {
            onSubscribe(observer)
        }
    }

fun errorCompletable(e: Throwable): Completable =
    completableByEmitter { emitter ->
        emitter.onError(e)
    }

fun Throwable.toErrorCompletable(): Completable = errorCompletable(this)

fun emptyCompletable(): Completable = completableByEmitter(CompletableEmitter::onComplete)

fun completableFromFunction(func: () -> Unit): Completable =
    completableByEmitter { emitter ->
        func()
        emitter.onComplete()
    }