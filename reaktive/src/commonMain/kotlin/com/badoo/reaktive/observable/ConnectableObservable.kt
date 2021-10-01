package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Connectable

/**
 * Resembles an ordinary [Observable], except that it does not begin emitting elements when it is subscribed to,
 * but only when its [Connectable.connect] method is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/observables/ConnectableObservable.html).
 */
interface ConnectableObservable<out T> : Observable<T>, Connectable
