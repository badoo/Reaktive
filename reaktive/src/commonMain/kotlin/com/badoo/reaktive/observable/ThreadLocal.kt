package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ThreadLocalStorage
import com.badoo.reaktive.utils.handleSourceError

fun <T> Observable<T>.threadLocal(): Observable<T> =
    observable {
        val disposables = CompositeDisposable()
        it.setDisposable(disposables)
        val emitterStorage = ThreadLocalStorage(it)
        disposables += emitterStorage

        subscribe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    getEmitter()?.onNext(value)
                }

                override fun onComplete() {
                    getEmitter()?.onComplete()
                }

                override fun onError(error: Throwable) {
                    getEmitter(error)?.onError(error)
                }

                private fun getEmitter(existingError: Throwable? = null): ObservableEmitter<T>? =
                    try {
                        requireNotNull(emitterStorage.get())
                    } catch (e: Throwable) {
                        handleSourceError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
