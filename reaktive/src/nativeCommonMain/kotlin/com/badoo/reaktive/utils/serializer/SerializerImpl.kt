package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.plusSorted

internal abstract class SerializerImpl<in T>(
    private val comparator: Comparator<in T>? = null
) : Serializer<T> {

    private val state = AtomicReference<State<T>?>(State())

    override fun accept(value: T) {
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

    override fun clear() {
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

    protected abstract fun onValue(value: T): Boolean

    private companion object {
        private fun <T> List<T>.addAndSort(item: T, comparator: Comparator<in T>?): List<T> =
            if (comparator == null) plus(item) else plusSorted(item, comparator)
    }

    private data class State<out T>(
        val queue: List<T> = emptyList(),
        val isDraining: Boolean = false
    )
}