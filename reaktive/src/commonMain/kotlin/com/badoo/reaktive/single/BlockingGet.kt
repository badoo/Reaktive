package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.PairReference
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.withLockAndCondition

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
