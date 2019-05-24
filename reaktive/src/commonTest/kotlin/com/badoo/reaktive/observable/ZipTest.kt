package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.dispose
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.isCompleted
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZipTest {

    private val sources = List(3) { TestObservable<Int>() }

    private val observer =
        sources
            .zip { it.joinToString(separator = ",") }
            .test()

    @Test
    fun does_no_emit_results_WHEN_not_not_all_sources_emitted() {
        sources[0].onNext(0)
        sources[2].onNext(2)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun emits_result_WHEN_all_sources_emitted() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onNext(1)

        assertEquals(listOf("0,1,2"), observer.values)
    }

    @Test
    fun does_not_emit_results_WHEN_not_all_sources_emitted_after_first_result_is_produced() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onNext(1)
        observer.reset()
        sources[0].onNext(10)
        sources[2].onNext(12)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun emits_second_result_WHEN_all_sources_emitted_after_first_result_is_produced() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onNext(1)
        observer.reset()
        sources[1].onNext(11)
        sources[0].onNext(10)
        sources[2].onNext(12)

        assertEquals(listOf("10,11,12"), observer.values)
    }

    @Test
    fun completes_WHEN_a_source_is_completed_without_a_value() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onComplete()

        assertTrue(observer.isCompleted)
    }


    @Test
    fun does_not_complete_WHEN_a_source_emitted_a_value_and_completed() {
        sources[0].onNext(0)
        sources[1].onNext(1)
        sources[1].onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun complete_WHEN_a_source_emitted_a_value_and_completed_and_first_result_is_produced() {
        sources[2].onNext(2)
        sources[2].onComplete()
        sources[0].onNext(0)
        observer.reset()
        sources[1].onNext(1)

        assertTrue(observer.isCompleted)
    }

    @Test
    fun produces_error_WHEN_a_source_emitted_a_value_and_produced_an_error() {
        val error = Throwable()

        sources[0].onNext(0)
        sources[2].onNext(2)
        observer.reset()
        sources[2].onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_all_sources_WHEN_completed() {
        sources[0].onComplete()

        assertTrue(sources[0].isDisposed)
        assertTrue(sources[1].isDisposed)
        assertTrue(sources[2].isDisposed)
    }

    @Test
    fun disposes_all_sources_WHEN_error() {
        sources[0].onError(Throwable())

        assertTrue(sources[0].isDisposed)
        assertTrue(sources[1].isDisposed)
        assertTrue(sources[2].isDisposed)
    }

    @Test
    fun disposes_all_sources_WHEN_disposed() {
        observer.dispose()

        assertTrue(sources[0].isDisposed)
        assertTrue(sources[1].isDisposed)
        assertTrue(sources[2].isDisposed)
    }
}