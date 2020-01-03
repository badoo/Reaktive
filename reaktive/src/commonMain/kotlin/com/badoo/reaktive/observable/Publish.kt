package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.subject.publish.PublishSubject

fun <T> Observable<T>.publish(): ConnectableObservable<T> = publish(::PublishSubject)

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
