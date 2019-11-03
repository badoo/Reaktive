package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

fun <T, R> Single<T>.flatMap(mapper: (T) -> Single<R>): Single<R> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch {
                        mapper(value).subscribe(
                            object : SingleObserver<R>, Observer by this, SingleCallbacks<R> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

fun <T, U, R> Single<T>.flatMap(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Single<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
