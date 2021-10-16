package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Observable],
 * disposing any previously subscribed inner [Observable]. Emits elements of a most recent inner [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMap-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.switchMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val innerSerialDisposable = SerialDisposable()
        disposables += innerSerialDisposable

        val state = AtomicReference(SwitchMapState())
        val serializedEmitter = emitter.serialize()

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by serializedEmitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    serializedEmitter.tryCatch(
                        block = { mapper(value) },
                        onSuccess = ::onInnerObservable
                    )
                }

                private fun onInnerObservable(observable: Observable<R>) {
                    val localSerialDisposable = SerialDisposable()

                    /*
                     * Dispose any existing inner Observable.
                     * If a previous Observable did not provide its disposable yet
                     * it will be disposed automatically later since
                     * its localSerialDisposable is disposed.
                     */
                    innerSerialDisposable.set(localSerialDisposable)

                    val innerObserver =
                        object : ObservableObserver<R>, ValueCallback<R> by serializedEmitter,
                            ErrorCallback by serializedEmitter {
                            override fun onSubscribe(disposable: Disposable) {
                                localSerialDisposable.set(disposable)
                            }

                            override fun onComplete() {
                                val actualState = state
                                    .updateAndGet { previousState ->
                                        if (previousState.innerObserver == this) {
                                            previousState.copy(innerObserver = null)
                                        } else {
                                            previousState
                                        }
                                    }
                                checkStateFinished(actualState)
                            }
                        }

                    state.update { previousState ->
                        previousState.copy(innerObserver = innerObserver)
                    }
                    observable.subscribeSafe(innerObserver)
                }

                override fun onComplete() {
                    val actualState =
                        state.updateAndGet { previousState -> previousState.copy(isUpstreamCompleted = true) }
                    checkStateFinished(actualState)
                }

                private fun checkStateFinished(state: SwitchMapState) {
                    if (state.isFinished) {
                        serializedEmitter.onComplete()
                    }
                }
            }
        )
    }

private data class SwitchMapState(
    val isUpstreamCompleted: Boolean = false,
    val innerObserver: Any? = null
) {
    val isFinished: Boolean get() = isUpstreamCompleted && (innerObserver == null)
}

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Observable],
 * disposing any previously subscribed inner [Observable].
 * For each element [U] emitted by a most recent inner [Observable], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#switchMap-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.switchMap(
    mapper: (T) -> Observable<U>,
    resultSelector: (T, U) -> R
): Observable<R> =
    switchMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
