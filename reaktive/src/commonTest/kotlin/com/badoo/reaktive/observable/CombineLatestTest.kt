package com.badoo.reaktive.observable

import com.badoo.reaktive.test.TestObservableRelay
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class CombineLatestTest {

    private val relay1 = TestObservableRelay<String?>()
    private val relay2 = TestObservableRelay<String?>()
    private val relay3 = TestObservableRelay<String?>()
    private val mapper = AtomicReference<(List<String?>) -> String?> { it.joinToString(separator = ",") }
    private val observer = createAndSubscribe(sources = listOf(relay1, relay2, relay3), mapper = mapper)

    private fun createAndSubscribe(
        sources: Iterable<Observable<String?>>,
        mapper: AtomicReference<(List<String?>) -> String?>
    ): TestObservableObserver<String?> =
        sources
            .combineLatest { mapper.value(it) }
            .test()

    @Test
    fun nothing_is_emitted_WHEN_not_all_sources_produced_values() {
        relay1.onNext("1")
        relay3.onNext("3")

        observer.assertNoValues()
    }

    @Test
    fun result_is_emitted_WHEN_all_sources_produced_values() {
        relay1.onNext("1")
        relay3.onNext("3")
        relay2.onNext("2")

        observer.assertValues("1,2,3")
    }

    @Test
    fun second_result_is_emitted_WHEN_additional_value_is_provided() {
        relay1.onNext("1")
        relay3.onNext("3")
        relay2.onNext("2")
        observer.reset()
        relay2.onNext("4")

        observer.assertValues("1,4,3")
    }

    @Test
    fun null_values_from_upstreams_are_allowed() {
        relay1.onNext(null)
        relay3.onNext(null)
        relay2.onNext(null)

        observer.assertValues("null,null,null")
    }

    @Test
    fun null_values_to_downstream_are_allowed() {
        mapper.value = { null }
        relay1.onNext(null)
        relay3.onNext(null)
        relay2.onNext(null)

        observer.assertValue(null)
    }

    @Test
    fun completed_without_any_values_WHEN_one_source_completed_without_a_value() {
        relay1.onNext("1")
        relay2.onNext("2")
        relay3.onComplete()

        observer.assertNoValues()
        observer.assertComplete()
    }

    @Test
    fun not_completed_WHEN_not_all_sources_produced_values_and_one_source_completed_after_it_produced_a_value() {
        relay1.onNext("1")
        relay2.onNext("2")
        relay2.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun not_completed_WHEN_all_sources_produced_values_and_one_source_completed() {
        relay1.onNext("1")
        relay2.onNext("2")
        relay3.onNext("3")
        relay2.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun erred_WHEN_one_source_erred_without_a_value() {
        val error = Throwable()

        relay1.onNext("1")
        relay2.onNext("2")
        relay3.onError(error)

        observer.assertError(error)
    }

    @Test
    fun erred_WHEN_not_all_sources_produced_values_and_one_source_erred_after_it_produced_a_value() {
        relay1.onNext("1")
        relay2.onNext("2")
        relay2.onError(Throwable())

        observer.assertNotComplete()
    }

    @Test
    fun erred_WHEN_all_sources_produced_values_and_one_source_erred() {
        val error = Throwable()

        relay1.onNext("1")
        relay2.onNext("2")
        relay3.onNext("3")
        relay2.onError(error)

        observer.assertError(error)
    }

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
    }

    @Test
    fun nothing_is_emitted_AFTER_disposed() {
        relay1.onNext("1")
        relay2.onNext("2")
        relay3.onNext("3")
        observer.reset()

        observer.dispose()
        relay1.onNext("4")

        observer.assertNoValues()
    }

    @Test
    fun completed_WHEN_sources_are_empty() {
        val observer = createAndSubscribe(sources = emptyList(), mapper = mapper)

        observer.assertComplete()
    }

}
