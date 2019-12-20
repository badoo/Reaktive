package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.completable.serialize
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Observable<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    completable { emitter ->
        val upstreamObserver = FlatMapCompletableObserver(emitter.serialize(), mapper)
        emitter.setDisposable(upstreamObserver)
        subscribe(upstreamObserver)
    }

private class FlatMapCompletableObserver<in T>(
    private val callbacks: CompletableCallbacks,
    private val mapper: (T) -> Completable
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

    private inner class InnerObserver : ObjectReference<Disposable?>(null), CompletableObserver, ErrorCallback by callbacks {
        override fun onSubscribe(disposable: Disposable) {
            value = disposable
            add(disposable)
        }

        override fun onComplete() {
            remove(value!!)
            this@FlatMapCompletableObserver.onComplete()
        }
    }
}
