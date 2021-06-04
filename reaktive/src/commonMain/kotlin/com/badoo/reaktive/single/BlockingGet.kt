package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.PairReference
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.withLockAndCondition

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
fun <T> Single<T>.blockingGet(): T =
    withLockAndCondition { lock, condition ->
        val observer =
            object : PairReference<Any?, Disposable?>(Uninitialized, null), SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    lock.synchronized {
                        second = disposable
                    }
                }

                override fun onSuccess(value: T) {
                    lock.synchronized {
                        first = value
                        condition.signal()
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        first = BlockingGetError(error)
                        condition.signal()
                    }
                }
            }

        subscribe(observer)

        lock.synchronized {
            while (observer.first === Uninitialized) {
                try {
                    condition.await()
                } catch (e: Throwable) {
                    observer.second?.dispose()
                    throw e
                }
            }
        }

        observer
            .first
            .let {
                if (it is BlockingGetError) {
                    throw it.error
                }

                @Suppress("UNCHECKED_CAST")
                it as T
            }
    }

private class BlockingGetError(val error: Throwable)
