package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

/**
 * Creates an [Observable] with manual signalling via [ObservableEmitter].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#create-io.reactivex.ObservableOnSubscribe-).
 */
inline fun <T> observable(crossinline onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val emitter = onSubscribeObservable(observer)
        emitter.tryCatch { onSubscribe(emitter) }
    }

@PublishedApi
internal fun <T> onSubscribeObservable(observer: ObservableObserver<T>): ObservableEmitter<T> =
    object : SerialDisposable(), ObservableEmitter<T> {
        override fun setDisposable(disposable: Disposable?) {
            set(disposable)
        }

        override fun onNext(value: T) {
            if (!isDisposed) {
                observer.onNext(value)
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
                val disposable: Disposable? = clearAndDispose()
                block()
                disposable?.dispose()
            }
        }
    }.also {
        observer.onSubscribe(it)
    }
