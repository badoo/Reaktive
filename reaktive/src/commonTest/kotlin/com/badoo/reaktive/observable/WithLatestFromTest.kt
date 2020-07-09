package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class WithLatestFromTest {

    private val source = TestObservable<Int>()
    private val other1 = TestObservable<Int>()
    private val other2 = TestObservable<Int>()
    private val other3 = TestObservable<Int>()

    private val observer = createAndSubscribe(listOf(other1, other2, other3))

    private fun createAndSubscribe(otherSources: Iterable<Observable<Int>>) =
        source
            .withLatestFrom(otherSources) { value, others -> listOf(value) + others }
            .test()

    @Test
    fun no_output_WHEN_source_emitted_one_value_and_then_all_others_emitted_its_values() {
        source.onNext(1)
        other1.onNext(2)
        other2.onNext(3)
        other3.onNext(4)

        observer.assertNoValues()
    }

    @Test
    fun no_output_WHEN_not_all_others_emitted_its_values_and_source_emitted_one_value() {
        other1.onNext(1)
        other3.onNext(2)
        source.onNext(3)

        observer.assertNoValues()
    }

    @Test
    fun no_output_WHEN_not_all_others_emitted_its_values_and_source_emitted_one_value_and_remaining_others_emitted_its_values() {
        other1.onNext(1)
        other3.onNext(2)
        source.onNext(3)
        other2.onNext(4)

        observer.assertNoValues()
    }

    @Test
    fun one_item_with_latest_values_emitted_WHEN_all_others_emitted_its_values_and_then_source_emitted_second_value() {
        other1.onNext(1)
        other2.onNext(2)
        other3.onNext(3)
        source.onNext(4)

        observer.assertValue(listOf(4, 1, 2, 3))
    }

    @Test
    fun one_item_with_latest_values_emitted_WHEN_source_emitted_one_value_and_then_all_others_emitted_its_values_and_then_source_emitted_second_value() {
        source.onNext(1)
        other1.onNext(2)
        other2.onNext(3)
        other3.onNext(4)
        source.onNext(5)

        observer.assertValue(listOf(5, 2, 3, 4))
    }

    @Test
    fun correctly_emits_complex_series() {
        other1.onNext(1)
        other2.onNext(2)
        source.onNext(3)
        source.onNext(4)
        other1.onNext(5)
        other3.onNext(6)
        source.onNext(7)
        other1.onNext(8)
        source.onNext(9)
        other3.onNext(10)
        other2.onNext(11)
        source.onNext(12)
        other1.onNext(13)
        other2.onNext(14)
        other3.onNext(15)

        observer.assertValues(listOf(7, 5, 2, 6), listOf(9, 8, 2, 6), listOf(12, 8, 11, 10))
    }

    @Test
    fun correctly_emits_complex_series_WHEN_others_are_empty() {
        val observer = createAndSubscribe(otherSources = emptyList())

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        observer.assertValues(listOf(1), listOf(2), listOf(3))
    }

}
