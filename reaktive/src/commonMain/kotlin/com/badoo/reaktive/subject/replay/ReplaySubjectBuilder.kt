package com.badoo.reaktive.subject.replay

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Creates a new instance of [ReplaySubject].
 *
 * @param bufferSize the maximum number of elements that the returned [ReplaySubject] will store and replay,
 * default value is [Int.MAX_VALUE]
 */
@Suppress("FunctionName")
fun <T> ReplaySubject(bufferSize: Int = Int.MAX_VALUE): ReplaySubject<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return object : DefaultSubject<T>(), ReplaySubject<T> {
        private val buffer = SharedQueue<T>()

        override fun onSubscribed(observer: ObservableObserver<T>): Boolean {
            buffer.forEach(observer::onNext)

            return true
        }

        override fun onBeforeNext(value: T) {
            super.onBeforeNext(value)

            if (buffer.size >= bufferSize) {
                buffer.poll()
            }
            buffer.offer(value)
        }
    }
}
