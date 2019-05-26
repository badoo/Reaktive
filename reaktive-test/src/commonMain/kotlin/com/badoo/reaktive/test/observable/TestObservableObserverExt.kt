package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.observable.TestObservableObserver.Event

fun <T> TestObservableObserver<T>.getOnNextEvent(index: Int): Event.OnNext<T> =
    events[index] as Event.OnNext<T>

fun <T> TestObservableObserver<T>.getOnNextValue(index: Int): T =
    getOnNextEvent(index).value

fun <T> TestObservableObserver<T>.isOnCompleteEvent(index: Int): Boolean =
    events.getOrNull(index) is Event.OnComplete

fun TestObservableObserver<*>.getOnErrorEvent(index: Int): Event.OnError =
    events[index] as Event.OnError

fun TestObservableObserver<*>.getOnErrorValue(index: Int): Throwable =
    getOnErrorEvent(index).error

val TestObservableObserver<*>.isCompleted: Boolean
    get() = (events.count { it is Event.OnComplete } == 1) && events.none { it is Event.OnError }

val TestObservableObserver<*>.isError: Boolean
    get() = (events.count { it is Event.OnError } == 1) && events.none { it is Event.OnComplete }

val TestObservableObserver<*>.hasOnNext: Boolean get() = events.any { it is Event.OnNext }

val <T> TestObservableObserver<T>.values: List<T>
    get() =
        events
            .takeWhile { it is TestObservableObserver.Event.OnNext }
            .map { (it as TestObservableObserver.Event.OnNext).value }

fun TestObservableObserver<*>.isError(error: Throwable): Boolean =
    isError && events.any { (it as? Event.OnError)?.error == error }

fun <T> Observable<T>.test(): TestObservableObserver<T> =
    TestObservableObserver<T>()
        .also(::subscribe)