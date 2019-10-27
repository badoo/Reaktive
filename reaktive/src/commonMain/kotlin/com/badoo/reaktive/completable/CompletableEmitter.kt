package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Emitter

/**
 * Represents an emitter for Completable source.
 * See [Emitter] and [CompletableCallbacks] for more information.
 */
interface CompletableEmitter : Emitter, CompletableCallbacks
