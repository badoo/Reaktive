package com.badoo.reaktive.testutils

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.testutils.TestObservableObserver.Event

fun <T> TestObservableObserver<T>.getOnNextEvent(index: Int): Event.OnNext<T> =
    events[index] as Event.OnNext<T>

fun <T> TestObservableObserver<T>.isOnCompleteEvent(index: Int): Boolean =
    events.getOrNull(index) is Event.OnComplete

fun TestObservableObserver<*>.getOnErrorEvent(index: Int): Event.OnError =
    events[index] as Event.OnError

val TestObservableObserver<*>.isCompleted: Boolean
    get() = (events.count { it is Event.OnComplete } == 1) && events.none { it is Event.OnError }

val TestObservableObserver<*>.isError: Boolean
    get() = (events.count { it is Event.OnError } == 1) && events.none { it is Event.OnComplete }

val TestObservableObserver<*>.hasOnNext: Boolean get() = events.any { it is Event.OnNext }

fun TestObservableObserver<*>.isError(error: Throwable): Boolean =
    isError && events.any { (it as? Event.OnError)?.error == error }

fun <T> Observable<T>.test(): TestObservableObserver<T> =
    TestObservableObserver<T>()
        .also(::subscribe)