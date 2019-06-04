package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.completable.TestCompletableObserver.Event

val TestCompletableObserver.isCompleted: Boolean
    get() = (events.count { it is Event.OnSuccess } == 1) && events.none { it is Event.OnError }

val TestCompletableObserver.isError: Boolean
    get() = (events.count { it is Event.OnError } == 1) && events.none { it is Event.OnSuccess }

fun TestCompletableObserver.isError(error: Throwable): Boolean =
    isError && events.any { (it as? Event.OnError)?.error == error }

fun Completable.test(): TestCompletableObserver =
    TestCompletableObserver()
        .also(::subscribe)