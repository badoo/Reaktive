package com.badoo.reaktive.subject.unicast

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.isActive
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.queue.SharedQueue

@Suppress("FunctionName")
fun <T> UnicastSubject(bufferSize: Int = Int.MAX_VALUE, onTerminate: () -> Unit = {}): UnicastSubject<T> =
    object : DefaultSubject<T>(), UnicastSubject<T> {
        private val hasSubscribers = AtomicBoolean()
        private val queue = AtomicReference<SharedQueue<T>?>(SharedQueue())

        override fun onSubscribed(observer: ObservableObserver<T>): Boolean {
            val isFirstObserver = hasSubscribers.compareAndSet(false, true)

            if (isFirstObserver) {
                queue
                    .getAndSet(null)
                    ?.forEach(observer::onNext)
            } else {
                observer.onError(IllegalStateException("Only a single observer allowed for UnicastSubject"))
            }

            return isFirstObserver
        }

        override fun onBeforeNext(value: T) {
            super.onBeforeNext(value)

            queue.value?.apply {
                if (size >= bufferSize) {
                    poll()
                }
                offer(value)
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
