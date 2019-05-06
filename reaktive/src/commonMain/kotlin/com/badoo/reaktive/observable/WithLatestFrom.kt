package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized
import kotlin.jvm.JvmName

fun <T, U, R> Observable<T>.withLatestFrom(
    other: Observable<U>,
    combine: (value: T, other: U) -> R
): Observable<R> =
    observable { emitter ->
        val proxy = WithLatestFromProxy(combine, emitter::onNext)
        val disposables = CompositeDisposable()

        emitter.setDisposable(disposables)

        this@withLatestFrom.subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {

                override fun onNext(value: T) = proxy.onNext(value)

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                    other.subscribeSafe(
                        object : ObservableObserver<U>, ErrorCallback by emitter {

                            override fun onSubscribe(disposable: Disposable) {
                                disposables += disposable
                            }

                            override fun onNext(value: U) = proxy.onNext(value)

                            override fun onComplete() {
                            }

                        }
                    )
                }
            }
        )
    }

private class WithLatestFromProxy<T, U, R>(
    private val combine: (value: T, other: U) -> R,
    private val onNext: (R) -> Unit
) {

    private val lock = newLock()
    private var value: Any? = Uninitialized
    private var other: Any? = Uninitialized

    @JvmName("onNextValue")
    fun onNext(value: T) {
        lock.synchronized {
            this.value = value
        }

        emit()
    }

    @JvmName("onNextOther")
    fun onNext(other: U) {
        lock.synchronized {
            this.other = other
        }

        emit()
    }

    @Suppress("UNCHECKED_CAST")
    private fun emit() {
        if (value != Uninitialized && other != Uninitialized) {
            combine(value as T, other as U).let(onNext)
        }
    }

}
