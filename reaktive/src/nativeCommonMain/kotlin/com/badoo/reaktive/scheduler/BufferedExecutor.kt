package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val state = AtomicReference(State<T>(), true)
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        val oldState =
            state.getAndUpdate {
                it.copy(
                    queue = it.queue + value,
                    isDraining = true
                )
            }

        if (!oldState.isDraining) {
            executor.submit(0, drainFunction)
        }
    }

    private fun drain() {
        while (true) {
            val oldState =
                state.getAndUpdate {
                    it.copy(
                        queue = it.queue.drop(1),
                        isDraining = it.queue.isNotEmpty()
                    )
                }

            if (oldState.queue.isEmpty()) {
                return
            }

            onNext(oldState.queue[0])
        }
    }

    private data class State<T>(
        val queue: List<T> = emptyList(),
        val isDraining: Boolean = false
    )
}