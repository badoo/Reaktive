package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized

/**
 * Blocks current thread until the current `Maybe` succeeds with a value (which is returned),
 * completes (`null` is returned) or fails with an exception (which is propagated).
 *
 * ⚠️ Please note that this method is not available in JavaScript due to its single threaded nature.
 * A runtime exception will be thrown when this method is called in JavaScript. If you need this
 * in JavaScript for testing purposes, then consider using `Single.testAwait()` extension
 * from the `reaktive-testing` module.
 */
fun <T> Maybe<T>.blockingGet(): T? {
    var successResult: T? = null
    var errorResult: Throwable? = null
    var isFinished = false
    var disposableRef: Disposable? = null

    val observer =
        object : ConditionLock(), MaybeObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                synchronized {
                    disposableRef = disposable
                }
            }

            override fun onSuccess(value: T) {
                synchronized {
                    successResult = value
                    isFinished = true
                    signal()
                }
            }

            override fun onComplete() {
                synchronized {
                    isFinished = true
                    signal()
                }
            }

            override fun onError(error: Throwable) {
                synchronized {
                    errorResult = error
                    isFinished = true
                    signal()
                }
            }
        }

    subscribe(observer)

    observer.synchronized {
        while (!isFinished) {
            try {
                observer.await()
            } catch (e: Throwable) {
                disposableRef?.dispose()
                throw e
            }
        }
    }

    errorResult?.also {
        throw it
    }

    return successResult
}
