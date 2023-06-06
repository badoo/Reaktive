package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

/**
 * Creates a [Single] with manual signalling via [SingleEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#create-io.reactivex.SingleOnSubscribe-).
 */
inline fun <T> single(crossinline onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter = onSubscribeSingle(observer)
        emitter.tryCatch { onSubscribe(emitter) }
    }

@PublishedApi
internal fun <T> onSubscribeSingle(observer: SingleObserver<T>): SingleEmitter<T> =
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
                val disposable: Disposable? = clearAndDispose()
                block()
                disposable?.dispose()
            }
        }
    }.also {
        observer.onSubscribe(it)
    }
