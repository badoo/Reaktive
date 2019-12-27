package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.ThreadLocalDisposableHolder
import com.badoo.reaktive.utils.handleReaktiveError

fun <T> Maybe<T>.threadLocal(): Maybe<T> =
    maybe {
        val disposables = CompositeDisposable()
        it.setDisposable(disposables)
        val emitterStorage = ThreadLocalDisposableHolder(it)
        disposables += emitterStorage

        subscribe(
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
                        handleReaktiveError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
