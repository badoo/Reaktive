package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Source

/**
 * Represents a [Source] that either completes or produces an error.
 *
 * There are a number of factory functions available, their names all begin with `completable*(...)`.
 *
 * See [Source] and [CompletableCallbacks] for more information.
 */
interface Completable : Source<CompletableObserver>
