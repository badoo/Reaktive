package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class SwitchIfEmptyTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ switchIfEmpty(observableOf(10)) }) {

    @Test
    fun should_switch_streams_when_source_is_empty() {
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onComplete()

        observer.assertValue(42)
    }

    @Test
    fun should_not_switch_streams_when_source_isnot_empty() {
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onNext(1)
        source.onComplete()

        observer.assertValue(1)
    }

    @Test
    fun should_not_switch_streams_when_source_emits_null() {
        val source = TestObservable<Int?>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onNext(null)
        source.onComplete()

        observer.assertValue(null)
    }

    @Test
    fun should_complete_when_both_sources_are_empty() {
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty(observableOfEmpty()).test()

        source.onComplete()

        observer.assertNoValues()
        observer.assertComplete()
    }

    @Test
    fun should_switch_streams_when_source_is_empty_using_lambda() {
        val source = TestObservable<Int>()
        val otherObservable = observableOf(42)

        val observer = source.switchIfEmpty { otherObservable }.test()

        source.onComplete()

        observer.assertValue(42)
    }

    @Test
    fun should_emit_error_when_lambda_throws() {
        val error = RuntimeException()
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty { throw error }.test()

        source.onComplete()

        observer.assertError(error)
    }
}
