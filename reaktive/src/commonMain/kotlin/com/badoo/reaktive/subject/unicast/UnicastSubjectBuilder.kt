package com.badoo.reaktive.subject.unicast

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.isActive

/**
 * Create a new instance of [UnicastSubject].
 *
 * @param bufferSize the maximum number of elements that the returned [UnicastSubject] will store and replay,
 * default value is [Int.MAX_VALUE]
 * @param onTerminate called when the returned [UnicastSubject] receives a terminal event (`onComplete` or `onError`)
 */
@Suppress("FunctionName")
fun <T> UnicastSubject(bufferSize: Int = Int.MAX_VALUE, onTerminate: () -> Unit = {}): UnicastSubject<T> =
    object : DefaultSubject<T>(), UnicastSubject<T> {
        private var queue: ArrayDeque<T>? = ArrayDeque()

        override fun onSubscribed(observer: ObservableObserver<T>): Boolean {
            queue?.also {
                queue = null
                it.forEach(observer::onNext)
                return true
            }

            observer.onError(IllegalStateException("Only a single observer allowed for UnicastSubject"))

            return false
        }

        override fun onBeforeNext(value: T) {
            super.onBeforeNext(value)

            queue?.apply {
                if (size >= bufferSize) {
                    removeFirst()
                }
                addLast(value)
            }
        }

        override fun onAfterUnsubscribe(observer: ObservableObserver<T>) {
            super.onAfterUnsubscribe(observer)

            if (isActive) {
                status = Subject.Status.Completed
            }
        }

        override fun onStatusChanged(status: Subject.Status) {
            super.onStatusChanged(status)

            if (!status.isActive) {
                onTerminate()
            }
        }
    }
