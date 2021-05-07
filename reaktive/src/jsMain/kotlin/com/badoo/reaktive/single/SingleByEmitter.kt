package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

// Don't inline at the moment for JS, we had random crashes in JS
/**
 * Creates a [Single] with manual signalling via [SingleEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#create-io.reactivex.SingleOnSubscribe-).
 */
actual fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter =
            object : SerialDisposable(), SingleEmitter<T> {
                override fun setDisposable(disposable: Disposable?) {
                    set(disposable)
                }

                override fun onSuccess(value: T) {
                    doIfNotDisposedAndDispose {
                        observer.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    doIfNotDisposedAndDispose {
                        observer.onError(error)
                    }
                }

                private inline fun doIfNotDisposedAndDispose(block: () -> Unit) {
                    if (!isDisposed) {
                        val disposable: Disposable? = replace(null)
                        try {
                            dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        observer.onSubscribe(emitter)
        emitter.tryCatch { onSubscribe(emitter) }
    }
