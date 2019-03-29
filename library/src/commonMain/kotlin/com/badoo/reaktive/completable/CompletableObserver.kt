package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Observer

/**
 * Represents an [Observer] of [Completable] source.
 * See [Observer] and [CompletableCallbacks] for more information.
 */
interface CompletableObserver : Observer, CompletableCallbacks