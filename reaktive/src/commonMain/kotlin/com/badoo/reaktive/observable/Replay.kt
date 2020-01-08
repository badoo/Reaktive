package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.subject.replay.ReplaySubject

fun <T> Observable<T>.replay(): ConnectableObservable<T> = replay(bufferSize = Int.MAX_VALUE)

fun <T> Observable<T>.replay(bufferSize: Int): ConnectableObservable<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return publish { ReplaySubject(bufferSize = bufferSize) }
}
