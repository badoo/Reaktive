package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T, R> Observable<T>.flatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val upstreamObserver = FlatMapObserver(emitter.serialize(), mapper)
        emitter.setDisposable(upstreamObserver)
        subscribe(upstreamObserver)
    }

private class FlatMapObserver<in T, in R>(
    private val callbacks: ObservableCallbacks<R>,
    private val mapper: (T) -> Observable<R>
) : CompositeDisposable(), ObservableObserver<T>, ErrorCallback by callbacks {

    private val activeSourceCount = AtomicInt(1)

    override fun onSubscribe(disposable: Disposable) {
        add(disposable)
    }

    override fun onNext(value: T) {
        activeSourceCount.addAndGet(1)

        callbacks.tryCatch {
            mapper(value).subscribe(InnerObserver())
        }
    }

    override fun onComplete() {
        if (activeSourceCount.addAndGet(-1) <= 0) {
            callbacks.onComplete()
        }
    }

    private inner class InnerObserver :
        ObjectReference<Disposable?>(null),
        ObservableObserver<R>,
        ErrorCallback by callbacks,
        ValueCallback<R> by callbacks {

        override fun onSubscribe(disposable: Disposable) {
            value = disposable
            add(disposable)
        }

        override fun onComplete() {
            remove(value!!)
            this@FlatMapObserver.onComplete()
        }
    }
}

fun <T, U, R> Observable<T>.flatMap(mapper: (T) -> Observable<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMap { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
