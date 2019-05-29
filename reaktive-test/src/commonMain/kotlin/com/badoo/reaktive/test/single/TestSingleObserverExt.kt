package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.single.TestSingleObserver.Event

val <T> TestSingleObserver<T>.value: T
    get() =
        if (isSuccess) {
            events
                .filterIsInstance<Event.OnSuccess<T>>()
                .first()
                .value
        } else {
            throw IllegalStateException("The Single did not succeed: $this")
        }

val TestSingleObserver<*>.isSuccess: Boolean
    get() = (events.count { it is Event.OnSuccess } == 1) && events.none { it is Event.OnError }

val TestSingleObserver<*>.isError: Boolean
    get() = (events.count { it is Event.OnError } == 1) && events.none { it is Event.OnSuccess }

fun TestSingleObserver<*>.isError(error: Throwable): Boolean =
    isError && events.any { (it as? Event.OnError)?.error == error }

fun <T> Single<T>.test(): TestSingleObserver<T> =
    TestSingleObserver<T>()
        .also(::subscribe)