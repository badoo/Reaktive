package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate

/**
 * Returns an [Observable] that connects to this [ConnectableObservable] when the number
 * of active subscribers reaches [subscriberCount] and disconnects when all subscribers have unsubscribed.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/observables/ConnectableObservable.html#refCount-int-).
 */
fun <T> ConnectableObservable<T>.refCount(subscriberCount: Int = 1): Observable<T> {
    require(subscriberCount > 0)

    val subscribeCount = AtomicInt()
    val disposable = AtomicReference<Disposable?>(null)

    return observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        disposables +=
            Disposable {
                if (subscribeCount.addAndGet(-1) == 0) {
                    disposable
                        .getAndUpdate { null }
                        ?.dispose()
                }
            }

        this@refCount.subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }
            }
        )

        if (subscribeCount.addAndGet(1) == subscriberCount) {
            this@refCount.connect {
                disposable.value = it
            }
        }
    }
}
