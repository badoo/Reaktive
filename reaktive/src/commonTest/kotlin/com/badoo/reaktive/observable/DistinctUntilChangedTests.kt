package com.badoo.reaktive.utils.arrayqueue

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.distinctUntilChanged
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.subscribe
import kotlin.test.Test
import kotlin.test.assertEquals


private data class Question(val answer: Int)

class DistinctUntilChangedTests {

    private val thirteen = Question(13)
    private val fortyTwo = Question(42)

    @Test
    fun distinctUntilChanged() {
        val actual = observableOf(1, 1, 2, 2, 3, 3)
            .distinctUntilChanged()
            .record()

        assertEquals(listOf(1, 2, 3), actual)
    }

    @Test
    fun `distinctUntilChanged with a keySelector`() {
        val actual = observableOf(thirteen, thirteen, fortyTwo, fortyTwo)
            .distinctUntilChanged(keySelector = { it.answer })
            .record()

        assertEquals(listOf(thirteen, fortyTwo), actual)
    }

    @Test
    fun `distinctUntilChanged that checks whether it is not the same instance`() {
        val actual = observableOf(thirteen, thirteen, fortyTwo, fortyTwo)
            .distinctUntilChanged(comparer = { l, r -> l !== r })
            .record()

        assertEquals(listOf(thirteen, fortyTwo), actual)
    }

    private fun <T> Observable<T>.record(): List<T> =
        mutableListOf<T>().apply {
            subscribe { value ->
                add(value)
            }
        }

}

