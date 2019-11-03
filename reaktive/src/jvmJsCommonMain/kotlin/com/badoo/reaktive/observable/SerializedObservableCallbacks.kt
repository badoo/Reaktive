package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.queue.ArrayQueue

internal actual open class SerializedObservableCallbacks<in T> actual constructor(
    private val delegate: ObservableCallbacks<T>
) : ObservableCallbacks<T> {

    private val queue = ArrayQueue<T>()
    private var isComplete: Boolean = false
    private var error: Throwable? = null
    private var isDraining = false
    private var isFinished = false
    private var isEmpty = true

    override fun onNext(value: T) {
        synchronized(queue) {
            if (isFinished) {
                return
            }

            if (isDraining) {
                queue.offer(value)
                isEmpty = false
                return
            }

            isDraining = true
        }

        delegate.onNext(value)
        drain()
    }

    override fun onComplete() {
        synchronized(queue) {
            if (isFinished) {
                return
            }

            isFinished = true

            if (isDraining) {
                isComplete = true
                isEmpty = false
                return
            }
        }

        delegate.onComplete()
    }

    override fun onError(error: Throwable) {
        synchronized(queue) {
            if (isFinished) {
                return
            }

            isFinished = true

            if (isDraining) {
                this.error = error
                isEmpty = false
                return
            }
        }

        delegate.onError(error)
    }

    private fun drain() {
        while (true) {
            var sendItem: Any? = Uninitialized

            synchronized(queue) {
                when {
                    isEmpty -> {
                        isDraining = false
                        return
                    }

                    !queue.isEmpty -> sendItem = queue.poll()

                    !isComplete && (error == null) -> {
                        isEmpty = true
                        isDraining = false
                        return
                    }
                }
            }

            when {
                sendItem !== Uninitialized -> {
                    @Suppress("UNCHECKED_CAST")
                    delegate.onNext(sendItem as T)
                }

                isComplete -> {
                    delegate.onComplete()
                    return
                }

                error != null -> {
                    delegate.onError(error!!)
                    return
                }
            }
        }
    }
}
