package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

fun <T, R> Maybe<T>.flatMap(mapper: (T) -> Maybe<R>): Maybe<R> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch {
                        mapper(value).subscribe(
                            object : MaybeObserver<R>, Observer by this, MaybeCallbacks<R> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

fun <T, U, R> Maybe<T>.flatMap(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Maybe<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
