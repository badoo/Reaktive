package com.badoo.reaktive.completable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
// Not inlined due to https://youtrack.jetbrains.com/issue/KT-44764
/**
 * Creates a [Completable] with manual signalling via [CompletableEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#create-io.reactivex.CompletableOnSubscribe-).
 */
actual fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val serialDisposable = SerialDisposable()
        observer.onSubscribe(serialDisposable)

        val emitter =
            object : CompletableEmitter {
                override val isDisposed: Boolean get() = serialDisposable.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    serialDisposable.set(disposable)
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
