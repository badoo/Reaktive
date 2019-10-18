package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ThreadLocalStorage
import com.badoo.reaktive.utils.handleSourceError

fun <T> Maybe<T>.threadLocal(): Maybe<T> =
    maybe {
        val disposables = CompositeDisposable()
        it.setDisposable(disposables)
        val emitterStorage = ThreadLocalStorage(it)
        disposables += emitterStorage

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onSuccess(value: T) {
                    getEmitter()?.onSuccess(value)
                }

                override fun onComplete() {
                    getEmitter()?.onComplete()
                }

                override fun onError(error: Throwable) {
                    getEmitter(error)?.onError(error)
                }

                private fun getEmitter(existingError: Throwable? = null): MaybeEmitter<T>? =
                    try {
                        requireNotNull(emitterStorage.get())
                    } catch (e: Throwable) {
                        handleSourceError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
    