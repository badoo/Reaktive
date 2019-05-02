package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

private fun <T> inEqualityComparer(l: T, r: T) = l != r

fun <T> Observable<T>.distinctUntilChanged(comparer: (T, T) -> Boolean = ::inEqualityComparer): Observable<T> =
    distinctUntilChanged({ it }, comparer)

@Suppress("UNCHECKED_CAST")
fun <T, R> Observable<T>.distinctUntilChanged(
    keySelector: (T) -> R,
    comparer: (R, R) -> Boolean = ::inEqualityComparer
): Observable<T> =
    observable { emitter ->
        subscribeSafe(object : ObservableObserver<T>, CompletableCallbacks by emitter {

            val lock = newLock()
            var cache: Any? = Uninitialized

            override fun onNext(value: T) {
                val previous = lock.synchronized {
                    val result = cache
                    cache = value
                    result
                }
                val next = try {
                    if (previous == Uninitialized || comparer(keySelector(previous as T), keySelector(value)))
                        value
                    else
                        null
                } catch (e: Throwable) {
                    emitter.onError(e)
                    return
                }

                next?.let(emitter::onNext)
            }

            override fun onSubscribe(disposable: Disposable) = emitter.setDisposable(disposable)

        })
    }
