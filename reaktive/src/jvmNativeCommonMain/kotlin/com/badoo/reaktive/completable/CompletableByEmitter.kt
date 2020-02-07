package com.badoo.reaktive.completable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

actual inline fun completable(crossinline onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), CompletableEmitter {
                override fun setDisposable(disposable: Disposable?) {
                    set(disposable)
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
