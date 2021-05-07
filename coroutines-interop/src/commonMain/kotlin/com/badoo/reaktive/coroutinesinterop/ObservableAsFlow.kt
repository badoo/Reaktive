package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.ensureNeverFrozen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

@ExperimentalCoroutinesApi
fun <T> Observable<T>.asFlow(): Flow<T> =
    channelFlow {
        channel.ensureNeverFrozen()

        val serialDisposable = SerialDisposable()

        val observer =
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onNext(value: T) {
                    channel.offer(value)
                }

                override fun onComplete() {
                    channel.close()
                }

                override fun onError(error: Throwable) {
                    channel.close(error)
                }
            }

        try {
            subscribe(observer)
        } catch (e: Throwable) {
            channel.close(e)
        }

        awaitClose(serialDisposable::dispose)
    }
