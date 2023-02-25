package com.badoo.reaktive.observable

import com.badoo.reaktive.subject.behavior.BehaviorObservable

/**
 * Same as [ObservableWrapper] but wraps and implements [BehaviorObservable] and exposes the current value.
 */
open class BehaviorObservableWrapper<out T : Any>(
    private val inner: BehaviorObservable<T>,
) : ObservableWrapper<T>(inner), BehaviorObservable<T> {

    override val value: T get() = inner.value
}

fun <T : Any> BehaviorObservable<T>.wrap(): BehaviorObservableWrapper<T> =
    BehaviorObservableWrapper(this)
