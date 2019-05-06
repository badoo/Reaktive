package com.badoo.reaktive.observable

import kotlin.test.Test
import kotlin.test.assertEquals

class WithLatestFromTest {

    private val combine: (Int, String) -> String = { value, other -> value.toString() + other }

    @Test
    fun combine_a_number_with_a_string() {
        val actual = observableOf(1)
            .withLatestFrom(observableOf("a"), combine)
            .record()

        assertEquals(listOf("1a"), actual)
    }

    @Test
    fun combine_while_other_is_completed() {
        var emitter: ObservableEmitter<Int>? = null
        val actual = observable<Int> { emitter = it }
            .withLatestFrom(observableOf("a"), combine)
            .record()

        emitter!!.onNext(1)

        assertEquals(listOf("1a"), actual)
    }

    private fun <T> Observable<T>.record(): List<T> =
        mutableListOf<T>().apply {
            subscribe { value ->
                add(value)
            }
        }

}
