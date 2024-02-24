package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.lock.Lock

/**
 * Returns an [Observable] that connects to this [ConnectableObservable] when the number
 * of active subscribers reaches [subscriberCount] and disconnects when all subscribers have unsubscribed.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/observables/ConnectableObservable.html#refCount-int-).
 */
fun <T> ConnectableObservable<T>.refCount(subscriberCount: Int = 1): Observable<T> {
    require(subscriberCount > 0)

    var subscribeCount = 0
    val lock = Lock()
    val connectionDisposable = SerialDisposable()

    return observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val shouldConnect = lock.synchronized { ++subscribeCount == subscriberCount }

        this@refCount.subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }
            }
        )

        if (shouldConnect) {
            this@refCount.connect(connectionDisposable::set)
        }

        disposables +=
            Disposable {
                lock.synchronized {
                    if (--subscribeCount == 0) {
                        connectionDisposable.set(null)
                    }
                }
            }
    }
}
