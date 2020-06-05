package com.badoo.reaktive.completable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
actual inline fun completable(crossinline onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : CompletableEmitter {
                override val isDisposed: Boolean get() = disposableWrapper.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    doIfNotDisposedAndDispose(block = observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    doIfNotDisposedAndDispose {
                        observer.onError(error)
                    }
                }

                private inline fun doIfNotDisposedAndDispose(block: () -> Unit) {
                    if (!isDisposed) {
                        val disposable: Disposable? = disposableWrapper.replace(null)
                        try {
                            disposableWrapper.dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        emitter.tryCatch { onSubscribe(emitter) }
    }
