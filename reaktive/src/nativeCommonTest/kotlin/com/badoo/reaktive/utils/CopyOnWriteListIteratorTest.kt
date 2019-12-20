package com.badoo.reaktive.utils

import kotlin.native.concurrent.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

class CopyOnWriteListIteratorTest {

    @Test
    fun listIterator() {
        val lists: Pair<MutableList<Int?>, MutableList<Int?>> = Pair(CopyOnWriteList<Int?>().freeze(), ArrayList())
        lists.first.addAll(listOf(0, null, 1, null, 2))
        lists.second.addAll(listOf(0, null, 1, null, 2))
        val iterators = Pair(lists.first.listIterator(), lists.second.listIterator())

        iterators.verifyState()
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { add(3) }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { add(4) }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { remove() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { remove() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { set(5) }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { previous() }
        iterators.actionAndVerify { remove() }
        iterators.actionAndVerify { next() }
        iterators.actionAndVerify { previous() }
        assertEquals(lists.second.size, lists.first.size)
    }

    private inline fun <T : MutableListIterator<*>, R> Pair<T, T>.actionAndVerify(block: T.() -> R) {
        val actual = first.block()
        val expected = second.block()
        assertEquals(expected, actual)
        verifyState()
    }

    private fun Pair<MutableListIterator<*>, MutableListIterator<*>>.verifyState() {
        assertEquals(second.hasNext(), first.hasNext())
        assertEquals(second.hasPrevious(), first.hasPrevious())
        assertEquals(second.nextIndex(), first.nextIndex())
        assertEquals(second.previousIndex(), first.previousIndex())
    }
}
