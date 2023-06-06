package com.badoo.reaktive.completable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

/**
 * Creates a [Completable] with manual signalling via [CompletableEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#create-io.reactivex.CompletableOnSubscribe-).
 */
inline fun completable(crossinline onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val emitter = onSubscribeCompletable(observer)
        emitter.tryCatch { onSubscribe(emitter) }
    }

@PublishedApi
internal fun onSubscribeCompletable(observer: CompletableObserver): CompletableEmitter =
    object : SerialDisposable(), CompletableEmitter {
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
                val disposable: Disposable? = clearAndDispose()
                block()
                disposable?.dispose()
            }
        }
    }.also {
        observer.onSubscribe(it)
    }
