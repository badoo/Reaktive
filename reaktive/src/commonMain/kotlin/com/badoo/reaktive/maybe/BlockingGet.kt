package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.withLockAndCondition

fun <T> Maybe<T>.blockingGet(): T? =
    withLockAndCondition { lock, condition ->
        val result = ObjectReference<Any?>(Uninitialized)
        val upstreamDisposable = ObjectReference<Disposable?>(null)

        subscribe(
            object : MaybeObserver<T> {
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

                override fun onComplete() {
                    lock.synchronized {
                        result.value = BlockingGetResult.Completed
                        condition.signal()
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        result.value = BlockingGetResult.Error(error)
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
                @Suppress("UNCHECKED_CAST")
                when (it) {
                    is BlockingGetResult.Completed -> null
                    is BlockingGetResult.Error -> throw it.error
                    else -> it as T
                }
            }
    }

private sealed class BlockingGetResult<out T> {
    object Completed : BlockingGetResult<Nothing>()
    class Error(val error: Throwable) : BlockingGetResult<Nothing>()
}
