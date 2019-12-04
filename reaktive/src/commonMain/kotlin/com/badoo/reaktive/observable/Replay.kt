package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.utils.queue.SharedQueue

fun <T> Observable<T>.replay(): ConnectableObservable<T> = replay(bufferSize = Int.MAX_VALUE)

fun <T> Observable<T>.replay(bufferSize: Int): ConnectableObservable<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return publish {
        object : DefaultSubject<T>() {
            private val values = SharedQueue<T>()

            override fun onAfterSubscribe(observer: ObservableObserver<T>) {
                super.onAfterSubscribe(observer)

                while (!values.isEmpty) {
                    @Suppress("UNCHECKED_CAST")
                    observer.onNext(values.poll() as T)
                }
            }

            override fun onBeforeNext(value: T) {
                super.onBeforeNext(value)

                if (values.size >= bufferSize) {
                    values.poll()
                }
                values.offer(value)
            }
        }
    }
}
