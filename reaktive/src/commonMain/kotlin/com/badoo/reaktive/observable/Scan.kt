package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

@Suppress("UNCHECKED_CAST")
fun <T> Observable<T>.scan(accumulate: (acc: T, value: T) -> T): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                val lock = newLock()
                var cache: Any? = Uninitialized

                override fun onNext(value: T) {
                    val previous = cache
                    val next = try {
                        when (previous) {
                            Uninitialized -> value
                            else -> accumulate(previous as T, value)
                        }
                    } catch (e: Throwable) {
                        emitter.onError(e)
                        return
                    }

                    lock.synchronized {
                        cache = next
                    }

                    emitter.onNext(next)
                }

                override fun onSubscribe(disposable: Disposable) =
                    emitter.setDisposable(disposable)

            }
        )
    }
