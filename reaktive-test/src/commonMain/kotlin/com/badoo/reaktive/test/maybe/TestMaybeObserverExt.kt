package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.maybe.TestMaybeObserver.Event

val <T> TestMaybeObserver<T>.value: T
    get() =
        if (isSuccess) {
            events
                .filterIsInstance<Event.OnSuccess<T>>()
                .first()
                .value
        } else {
            throw IllegalStateException("The Single did not succeed: $this")
        }

val TestMaybeObserver<*>.isSuccess: Boolean
    get() = (events.count { it is Event.OnSuccess } == 1) && events.none { it is Event.OnError || it is Event.OnComplete }

val TestMaybeObserver<*>.isComplete: Boolean
    get() = (events.count { it is Event.OnComplete } == 1) && events.none { it is Event.OnError || it is Event.OnSuccess }

val TestMaybeObserver<*>.isError: Boolean
    get() = (events.count { it is Event.OnError } == 1) && events.none { it is Event.OnSuccess || it is Event.OnComplete }

fun TestMaybeObserver<*>.isError(error: Throwable): Boolean =
    isError && events.any { (it as? Event.OnError)?.error == error }

fun <T> Maybe<T>.test(): TestMaybeObserver<T> =
    TestMaybeObserver<T>()
        .also(::subscribe)