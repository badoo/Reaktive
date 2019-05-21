package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback

/**
 * Callbacks for [Completable] source.
 * See [Completable], [CompleteCallback] and [ErrorCallback] for more information.
 */
interface CompletableCallbacks : CompleteCallback, ErrorCallback