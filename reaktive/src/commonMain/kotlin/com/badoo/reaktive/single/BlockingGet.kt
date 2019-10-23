package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.withLockAndCondition

fun <T> Single<T>.blockingGet(): T =
    withLockAndCondition { lock, condition ->
        val result = ObjectReference<Any?>(Uninitialized)
        val upstreamDisposable = ObjectReference<Disposable?>(null)

        subscribe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    lock.synchronized {
                        upstreamDisposable.value = disposable
                    }
                }

                override fun onSuccess(value: T) {
                    lock.synchronized {
                        result.value = value
                        condition.signal()
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        result.value = BlockingGetError(error)
                        condition.signal()
                    }
                }
            }
        )

        lock.synchronized {
            while (result.value === Uninitialized) {
                try {
                    condition.await()
                } catch (e: Throwable) {
                    upstreamDisposable.value?.dispose()
                    throw e
                }
            }
        }

        result
            .value
            .let {
                if (it is BlockingGetError) {
                    throw it.error
                }

                @Suppress("UNCHECKED_CAST")
                it as T
            }
    }

private class BlockingGetError(val error: Throwable)
