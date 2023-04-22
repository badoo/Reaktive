package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized

/**
 * Blocks current thread until the current [Single] succeeds with a value (which is returned) or
 * fails with an exception (which is propagated).
 *
 * ⚠️ Please note that this method is not available in JavaScript due to its single threaded nature.
 * A runtime exception will be thrown when this method is called in JavaScript. If you need this
 * in JavaScript for testing purposes, then consider using `Single.testAwait()` extension
 * from the `reaktive-testing` module.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#blockingGet--).
 */
fun <T> Single<T>.blockingGet(): T {
    var successResult: T? = null
    var errorResult: Throwable? = null
    var isFinished = false
    var disposableRef: Disposable? = null

    val observer =
        object : ConditionLock(), SingleObserver<T> {
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

    @Suppress("UNCHECKED_CAST") // successResult is guaranteed to be assigned
    return successResult as T
}
