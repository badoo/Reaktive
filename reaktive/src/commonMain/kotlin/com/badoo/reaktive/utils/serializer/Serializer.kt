package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.getAndUpdate
import com.badoo.reaktive.utils.atomicreference.update

/**
 * Serializes all calls to "accept" method and synchronously calls "onValue" method with corresponding values
 */
internal abstract class Serializer<in T>(
    private val comparator: Comparator<in T>? = null
) {

    private val state = AtomicReference<State<T>?>(State(), true)

    /**
     * Either calls "onValue" with the specified value or queues the value.
     * This method is supposed to be called from multiple threads.
     * If there are no threads currently processing any value then this thread will process the specified value.
     * Otherwise value will be queued and processed later by existing thread.
     *
     * @param value the value
     */
    fun accept(value: T) {
        state
            .getAndUpdate { state ->
                state?.copy(
                    queue = state.queue.addAndSort(value, comparator),
                    isDraining = true
                )
            }
            ?.isDraining
            ?.takeUnless { it }
            ?.run { drain() }
    }

    fun clear() {
        state.update {
            it?.copy(queue = emptyList())
        }
    }

    private fun drain() {
        while (true) {
            val oldState =
                state.getAndUpdate {
                    it?.copy(
                        queue = it.queue.drop(1),
                        isDraining = it.queue.isNotEmpty()
                    )
                }

            if ((oldState == null) || oldState.queue.isEmpty()) {
                return
            }

            if (!onValue(oldState.queue[0])) {
                state.value = null
                return
            }
        }
    }

    /**
     * Called synchronously for every value
     *
     * @param value a value
     * @return true if processing should continue, false otherwise
     */
    protected abstract fun onValue(value: T): Boolean

    private companion object {
        private fun <T> List<T>.addAndSort(item: T, comparator: Comparator<in T>?): List<T> {
            val list = ArrayList<T>(size + 1)
            list.addAll(this)
            list.add(item)
            if (comparator != null) {
                list.sortWith(comparator)
            }

            return list
        }
    }

    private data class State<out T>(
        val queue: List<T> = emptyList(),
        val isDraining: Boolean = false
    )
}