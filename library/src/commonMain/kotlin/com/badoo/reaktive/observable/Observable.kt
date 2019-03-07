package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Source

/**
 * Represents a [Source] that produces stream of values
 */
interface Observable<out T> : Source<ObservableObserver<T>>