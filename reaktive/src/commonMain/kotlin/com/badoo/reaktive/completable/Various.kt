package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable

inline fun completableUnsafe(crossinline onSubscribe: (observer: CompletableObserver) -> Unit): Completable =
    object : Completable {
        override fun subscribe(observer: CompletableObserver) {
            onSubscribe(observer)
        }
    }

fun completableOfError(error: Throwable): Completable =
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

fun Throwable.toCompletableOfError(): Completable = completableOfError(this)

fun completableOfEmpty(): Completable =
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }

fun completableOfNever(): Completable =
    completableUnsafe { observer ->
        observer.onSubscribe(Disposable())
    }

fun completableFromFunction(func: () -> Unit): Completable =
    completable { emitter ->
        func()
        emitter.onComplete()
    }
