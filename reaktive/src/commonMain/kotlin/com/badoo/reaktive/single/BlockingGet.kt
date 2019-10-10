package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.use

@Suppress("NestedBlockDepth")
fun <T> Single<T>.blockingGet(): T =
    Lock().use { lock ->
        lock.newCondition().use { condition ->
            val result = AtomicReference<BlockingGetResult<T>?>(null)
            val upstreamDisposable = AtomicReference<Disposable?>(null)

            subscribe(
                object : SingleObserver<T> {
                    override fun onSubscribe(disposable: Disposable) {
                        upstreamDisposable.value = disposable
                    }

                    override fun onSuccess(value: T) {
                        lock.synchronized {
                            result.value = BlockingGetResult.Success(value)
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
                while (result.value == null) {
                    try {
                        condition.await()
                    } catch (e: Throwable) {
                        upstreamDisposable.value?.dispose()
                        throw e
                    }
                }
            }

            result
                .value!!
                .let {
                    when (it) {
                        is BlockingGetResult.Success -> it.value
                        is BlockingGetResult.Error -> throw it.error
                    }
                }
        }
    }

private sealed class BlockingGetResult<out T> {
    class Success<out T>(val value: T) : BlockingGetResult<T>()
    class Error(val error: Throwable) : BlockingGetResult<Nothing>()
}
