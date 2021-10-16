package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.subject.publish.PublishSubject

/**
 * Returns a [ConnectableObservable] that shares a single subscription to the source [Observable].
 * The source [Observable] is subscribed when the [ConnectableObservable.connect] method is called,
 * and is unsubscribed when the returned [Disposable][com.badoo.reaktive.disposable.Disposable] is disposed.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#publish--).
 */
fun <T> Observable<T>.publish(): ConnectableObservable<T> = publish(::PublishSubject)

/**
 * For every subscription, calls the [selector] with an [Observable] that shares a single subscription to this [Observable],
 * and emits elements from the returned [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#publish-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.publish(selector: (Observable<T>) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val upstreamObserver =
            object : CompositeDisposableObserver(), ObservableObserver<R>, ObservableCallbacks<R> by emitter {
            }

        emitter.setDisposable(upstreamObserver)
        val published = publish()
        selector(published).subscribe(upstreamObserver)
        published.connect { upstreamObserver += it }
    }
