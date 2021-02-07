package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleCompletable
import kotlin.native.concurrent.SharedImmutable

@OptIn(ExperimentalReaktiveApi::class)
inline fun completableUnsafe(crossinline onSubscribe: (observer: CompletableObserver) -> Unit): Completable =
    onAssembleCompletable(
        object : Completable {
            override fun subscribe(observer: CompletableObserver) {
                onSubscribe(observer)
            }
        }
    )

fun completableOfError(error: Throwable): Completable =
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

fun Throwable.toCompletableOfError(): Completable = completableOfError(this)

@SharedImmutable
private val completableOfEmpty =
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }

fun completableOfEmpty(): Completable = completableOfEmpty

@SharedImmutable
private val completableOfNever =
    completableUnsafe { observer ->
        observer.onSubscribe(Disposable())
    }

fun completableOfNever(): Completable = completableOfNever

fun completableFromFunction(func: () -> Unit): Completable =
    completable { emitter ->
        func()
        emitter.onComplete()
    }
