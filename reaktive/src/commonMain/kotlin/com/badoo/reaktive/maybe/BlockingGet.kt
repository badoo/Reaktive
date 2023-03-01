package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.Lock
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
    val lock = Lock()
    val condition = lock.newCondition()

    var successResult: T? = null
    var errorResult: Throwable? = null
    var isFinished = false
    var disposableRef: Disposable? = null

    val observer =
        object : MaybeObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                lock.synchronized {
                    disposableRef = disposable
                }
            }

            override fun onSuccess(value: T) {
                lock.synchronized {
                    successResult = value
                    isFinished = true
                    condition.signal()
                }
            }

            override fun onComplete() {
                lock.synchronized {
                    isFinished = true
                    condition.signal()
                }
            }

            override fun onError(error: Throwable) {
                lock.synchronized {
                    errorResult = error
                    isFinished = true
                    condition.signal()
                }
            }
        }

    subscribe(observer)

    lock.synchronized {
        while (!isFinished) {
            try {
                condition.await()
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
