package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.PairReference
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.withLockAndCondition

/**
 * Blocks current thread until the current `Maybe` succeeds with a value (which is returned),
 * completes (`null` is returned) or fails with an exception (which is propagated).
 *
 * ⚠️ Please note that this method is not available in JavaScript due to its single threaded nature.
 * A runtime exception will be thrown when this method is called in JavaScript. If you need this
 * in JavaScript for testing purposes, then consider using `Single.testAwait()` extension
 * from the `reaktive-testing` module.
 */
fun <T> Maybe<T>.blockingGet(): T? =
    withLockAndCondition { lock, condition ->
        val observer =
            object : PairReference<Any?, Disposable?>(Uninitialized, null), MaybeObserver<T> {
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

                override fun onComplete() {
                    lock.synchronized {
                        first = BlockingGetResult.Completed
                        condition.signal()
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        first = BlockingGetResult.Error(error)
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
                @Suppress("UNCHECKED_CAST")
                when (it) {
                    BlockingGetResult.Completed -> null
                    is BlockingGetResult.Error -> throw it.error
                    else -> it as T
                }
            }
    }

private sealed class BlockingGetResult<out T> {
    object Completed : BlockingGetResult<Nothing>()
    class Error(val error: Throwable) : BlockingGetResult<Nothing>()
}
