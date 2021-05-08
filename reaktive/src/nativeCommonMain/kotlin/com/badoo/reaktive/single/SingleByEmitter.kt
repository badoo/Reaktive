package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
// Not inlined due to https://youtrack.jetbrains.com/issue/KT-44764
/**
 * Creates a [Single] with manual signalling via [SingleEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#create-io.reactivex.SingleOnSubscribe-).
 */
actual fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val serialDisposable = SerialDisposable()
        observer.onSubscribe(serialDisposable)

        val emitter =
            object : SingleEmitter<T> {
                override val isDisposed: Boolean get() = serialDisposable.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    serialDisposable.set(disposable)
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
                        val disposable: Disposable? = serialDisposable.replace(null)
                        try {
                            serialDisposable.dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        emitter.tryCatch { onSubscribe(emitter) }
    }
