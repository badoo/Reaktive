package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Subscribable

/**
 * Represents an observer of Completable source.
 * See [Subscribable] and [CompletableCallbacks] for more information.
 */
interface CompletableObserver : Subscribable, CompletableCallbacks