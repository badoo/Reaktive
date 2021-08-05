package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
// Not inlined due to https://youtrack.jetbrains.com/issue/KT-44764
/**
 * Creates a [Maybe] with manual signalling via [MaybeEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#create-io.reactivex.MaybeOnSubscribe-).
 */
actual fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val serialDisposable = SerialDisposable()
        observer.onSubscribe(serialDisposable)

        val emitter =
            object : MaybeEmitter<T> {
                override val isDisposed: Boolean get() = serialDisposable.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    serialDisposable.set(disposable)
                }

                override fun onSuccess(value: T) {
                    doIfNotDisposedAndDispose {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    doIfNotDisposedAndDispose(observer::onComplete)
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
