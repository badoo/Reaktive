package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Observable<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)

            return@observableUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeNext(consumer: (T) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    observer.tryCatch({ consumer(value) }) {
                        observer.onNext(value)
                    }
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeComplete(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    tryCatch(action) {
                        observer.onComplete()
                    }
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer, CompleteCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch({ consumer(error) }, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeTerminate(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(action) {
                        observer.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(action, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeDispose(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(action)
                    disposableWrapper.dispose()
                }
            }
        )

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeFinally(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val isFinished = AtomicBoolean()

        val onFinally =
            {
                @Suppress("BooleanLiteralArgument") // Not allowed for expected classes
                if (isFinished.compareAndSet(false, true)) {
                    action()
                }
            }

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(onFinally)
                    disposableWrapper.dispose()
                }
            }
        )

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(onFinally) {
                        observer.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(onFinally, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }
