package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.subject.replay.ReplaySubject

/**
 * Returns a [ConnectableObservable] that shares a single subscription to the source [Observable]
 * and replays all of its signals to any future subscriber.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#replay--).
 */
fun <T> Observable<T>.replay(): ConnectableObservable<T> = replay(bufferSize = Int.MAX_VALUE)

/**
 * Returns a [ConnectableObservable] that shares a single subscription to the source [Observable]
 * and replays at most [bufferSize] elements emitted by the source [Observable] to any future subscriber.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#replay-int-).
 */
fun <T> Observable<T>.replay(bufferSize: Int): ConnectableObservable<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return publish { ReplaySubject(bufferSize = bufferSize) }
}
