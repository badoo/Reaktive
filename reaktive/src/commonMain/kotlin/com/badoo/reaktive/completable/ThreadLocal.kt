package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.handleReaktiveError
import com.badoo.reaktive.utils.isolate.IsolatedReference
import com.badoo.reaktive.utils.isolate.getValue

fun Completable.threadLocal(): Completable =
    completable {
        val emitter by IsolatedReference(it)

        subscribe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    getEmitter()?.setDisposable(disposable)
                }

                override fun onComplete() {
                    getEmitter()?.onComplete()
                }

                override fun onError(error: Throwable) {
                    getEmitter(error)?.onError(error)
                }

                private fun getEmitter(existingError: Throwable? = null): CompletableEmitter? =
                    try {
                        emitter
                    } catch (e: Throwable) {
                        handleReaktiveError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
