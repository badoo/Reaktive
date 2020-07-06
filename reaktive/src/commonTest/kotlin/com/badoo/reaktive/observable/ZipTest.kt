package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse

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

        observer.assertNoValues()
    }

    @Test
    fun emits_result_WHEN_all_sources_emitted() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onNext(1)

        observer.assertValue("0,1,2")
    }

    @Test
    fun does_not_emit_results_WHEN_not_all_sources_emitted_after_first_result_is_produced() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onNext(1)
        observer.reset()
        sources[0].onNext(10)
        sources[2].onNext(12)

        observer.assertNoValues()
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

        observer.assertValue("10,11,12")
    }

    @Test
    fun completes_WHEN_a_source_is_completed_without_a_value() {
        sources[2].onNext(2)
        sources[0].onNext(0)
        sources[1].onComplete()

        observer.assertComplete()
    }


    @Test
    fun does_not_complete_WHEN_a_source_emitted_a_value_and_completed() {
        sources[0].onNext(0)
        sources[1].onNext(1)
        sources[1].onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun complete_WHEN_a_source_emitted_a_value_and_completed_and_first_result_is_produced() {
        sources[2].onNext(2)
        sources[2].onComplete()
        sources[0].onNext(0)
        observer.reset()
        sources[1].onNext(1)

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_a_source_emitted_a_value_and_produced_an_error() {
        val error = Throwable()

        sources[0].onNext(0)
        sources[2].onNext(2)
        observer.reset()
        sources[2].onError(error)

        observer.assertError(error)
    }

    @Test
    fun unsubscribes_from_all_sources_WHEN_completed() {
        sources[0].onComplete()

        sources.forEach {
            assertFalse(it.hasSubscribers)
        }
    }

    @Test
    fun unsubscribes_from_all_sources_WHEN_error() {
        sources[0].onError(Throwable())

        sources.forEach {
            assertFalse(it.hasSubscribers)
        }
    }

    @Test
    fun unsubscribes_from_all_sources_WHEN_disposed() {
        observer.dispose()

        sources.forEach {
            assertFalse(it.hasSubscribers)
        }
    }

    @Test
    fun completed_WHEN_sources_are_empty() {
        val observer = emptyList<Observable<Any>>().zip { it }.test()

        observer.assertComplete()
    }
}
