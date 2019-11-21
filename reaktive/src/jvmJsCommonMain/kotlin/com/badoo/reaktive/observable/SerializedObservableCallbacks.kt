package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.Queue

internal actual open class SerializedObservableCallbacks<in T> actual constructor(
    private val delegate: ObservableCallbacks<T>
) : ObservableCallbacks<T> {

    private var queue: Queue<T>? = null
    private var isComplete: Boolean = false
    private var error: Throwable? = null
    private var isDraining = false
    private var isFinished = false
    private var isEmpty = true

    override fun onNext(value: T) {
        synchronized(this) {
            if (isFinished) {
                return
            }

            if (isDraining) {
                val q = queue ?: ArrayQueue<T>().also { queue = it }
                q.offer(value)
                isEmpty = false
                return
            }

            isDraining = true
        }

        delegate.onNext(value)
        drain()
    }

    override fun onComplete() {
        synchronized(this) {
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
        synchronized(this) {
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

            synchronized(this) {
                when {
                    isEmpty -> {
                        isDraining = false
                        return
                    }

                    queue?.isEmpty == false -> sendItem = queue!!.poll()

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
