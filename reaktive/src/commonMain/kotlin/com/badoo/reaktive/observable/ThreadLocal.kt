package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.handleReaktiveError
import com.badoo.reaktive.utils.isolate.IsolatedReference
import com.badoo.reaktive.utils.isolate.getValue

fun <T> Observable<T>.threadLocal(): Observable<T> =
    observable {
        val emitter by IsolatedReference(it)

        subscribe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    getEmitter()?.setDisposable(disposable)
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
                        emitter
                    } catch (e: Throwable) {
                        handleReaktiveError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
