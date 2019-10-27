package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ThreadLocalStorage
import com.badoo.reaktive.utils.handleSourceError

fun Completable.threadLocal(): Completable =
    completable {
        val disposables = CompositeDisposable()
        it.setDisposable(disposables)
        val emitterStorage = ThreadLocalStorage(it)
        disposables += emitterStorage

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onComplete() {
                    getEmitter()?.onComplete()
                }

                override fun onError(error: Throwable) {
                    getEmitter(error)?.onError(error)
                }

                private fun getEmitter(existingError: Throwable? = null): CompletableEmitter? =
                    try {
                        requireNotNull(emitterStorage.get())
                    } catch (e: Throwable) {
                        handleSourceError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
