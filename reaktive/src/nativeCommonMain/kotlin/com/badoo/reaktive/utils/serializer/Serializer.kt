package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.getAndUpdate
import com.badoo.reaktive.utils.atomicreference.update

internal actual abstract class Serializer<in T> actual constructor(
    private val comparator: Comparator<in T>?
) {

    private val state = AtomicReference<State<T>?>(State(), true)

    actual fun accept(value: T) {
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

    actual fun clear() {
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

    protected actual abstract fun onValue(value: T): Boolean

    private companion object {
        private fun <T> List<T>.addAndSort(item: T, comparator: Comparator<in T>?): List<T> {
            val list = ArrayList<T>(size + 1)
            list.addAll(this)
            list.add(item)
            if (comparator != null) {
                list.sortWith(comparator) // TODO: Optimise later
            }

            return list
        }
    }

    private data class State<out T>(
        val queue: List<T> = emptyList(),
        val isDraining: Boolean = false
    )
}