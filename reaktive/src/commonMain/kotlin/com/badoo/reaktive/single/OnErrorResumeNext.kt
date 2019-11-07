package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

fun <T> Single<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Single<T>): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch({ nextSupplier(error) }, { CompositeException(error, it) }) {
                        it.subscribeSafe(
                            object : SingleObserver<T>, Observer by this, SingleCallbacks<T> by emitter {
                            }
                        )
                    }
                }
            }
        )
    }

fun <T> Single<T>.onErrorResumeNext(next: Single<T>): Single<T> =
    onErrorResumeNext { next }
