package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class CombineLatestTest {

    private lateinit var emitter1: ObservableEmitter<String?>
    private lateinit var emitter2: ObservableEmitter<String?>
    private lateinit var emitter3: ObservableEmitter<String?>
    private var mapper: (List<String?>) -> String? = { it.joinToString(separator = ",") }

    private val observer =
        listOf<Observable<String?>>(
            observable { emitter1 = it },
            observable { emitter2 = it },
            observable { emitter3 = it }
        )
            .combineLatest { mapper(it) }
            .test()

    @Test
    fun nothing_is_emitted_WHEN_not_all_sources_produced_values() {
        emitter1.onNext("1")
        emitter3.onNext("3")

        observer.assertNoValues()
    }

    @Test
    fun result_is_emitted_WHEN_all_sources_produced_values() {
        emitter1.onNext("1")
        emitter3.onNext("3")
        emitter2.onNext("2")

        observer.assertValues("1,2,3")
    }

    @Test
    fun second_result_is_emitted_WHEN_additional_value_is_provided() {
        emitter1.onNext("1")
        emitter3.onNext("3")
        emitter2.onNext("2")
        observer.reset()
        emitter2.onNext("4")

        observer.assertValues("1,4,3")
    }

    @Test
    fun null_values_from_upstreams_are_allowed() {
        emitter1.onNext(null)
        emitter3.onNext(null)
        emitter2.onNext(null)

        observer.assertValues("null,null,null")
    }

    @Test
    fun null_values_to_downstream_are_allowed() {
        mapper = { null }
        emitter1.onNext(null)
        emitter3.onNext(null)
        emitter2.onNext(null)

        observer.assertValue(null)
    }

    @Test
    fun completed_without_any_values_WHEN_one_source_completed_without_a_value() {
        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter3.onComplete()

        observer.assertNoValues()
        observer.assertComplete()
    }

    @Test
    fun not_completed_WHEN_not_all_sources_produced_values_and_one_source_completed_after_it_produced_a_value() {
        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter2.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun not_completed_WHEN_all_sources_produced_values_and_one_source_completed() {
        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter3.onNext("3")
        emitter2.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun erred_WHEN_one_source_erred_without_a_value() {
        val error = Throwable()

        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter3.onError(error)

        observer.assertError(error)
    }

    @Test
    fun erred_WHEN_not_all_sources_produced_values_and_one_source_erred_after_it_produced_a_value() {
        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter2.onError(Throwable())

        observer.assertNotComplete()
    }

    @Test
    fun erred_WHEN_all_sources_produced_values_and_one_source_erred() {
        val error = Throwable()

        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter3.onNext("3")
        emitter2.onError(error)

        observer.assertError(error)
    }

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
    }

    @Test
    fun nothing_is_emitted_AFTER_disposed() {
        emitter1.onNext("1")
        emitter2.onNext("2")
        emitter3.onNext("3")
        observer.reset()

        observer.dispose()
        emitter1.onNext("4")

        observer.assertNoValues()
    }
}