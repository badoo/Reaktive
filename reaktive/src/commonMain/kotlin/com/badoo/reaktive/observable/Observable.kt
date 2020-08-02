package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Source

/**
 * Represents a [Source] that begins with producing values (optional) and then completes or produces an exception.
 *
 * There are a number of factory functions available, their names all begin with `observable*(...)`.
 *
 * See [Source] and [ObservableCallbacks] for more information.
 */
interface Observable<out T> : Source<ObservableObserver<T>>
