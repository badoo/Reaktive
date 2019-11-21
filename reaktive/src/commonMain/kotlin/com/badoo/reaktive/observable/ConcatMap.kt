package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.updateAndGet

fun <T, R> Observable<T>.concatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val upstreamObserver = ConcatMapObserver(emitter.serialize(), mapper)
        emitter.setDisposable(upstreamObserver)
        subscribe(upstreamObserver)
    }

private class ConcatMapObserver<in T, in R>(
    private val callbacks: ObservableCallbacks<R>,
    private val mapper: (T) -> Observable<R>
) : CompositeDisposable(), ObservableObserver<T>, ErrorCallback by callbacks {

    private val state = AtomicReference(State<T>())

    override fun onSubscribe(disposable: Disposable) {
        add(disposable)
    }

    override fun onNext(value: T) {
        val oldState =
            state.getAndUpdate {
                it.copy(
                    queue = if (it.isMappedSourceActive) it.queue + value else it.queue,
                    isMappedSourceActive = true
                )
            }

        if (!oldState.isMappedSourceActive) {
            mapAndSubscribe(value)
        }
    }

    override fun onComplete() {
        val newState =
            state.updateAndGet {
                it.copy(isUpstreamCompleted = true)
            }

        if (newState.isUpstreamCompleted && !newState.isMappedSourceActive) {
            callbacks.onComplete()
        }
    }

    private fun mapAndSubscribe(value: T) {
        callbacks.tryCatch(block = { mapper(value) }) {
            it.subscribeSafe(InnerObserver())
        }
    }

    private data class State<out T>(
        val queue: List<T> = emptyList(),
        val isMappedSourceActive: Boolean = false,
        val isUpstreamCompleted: Boolean = false
    )

    private inner class InnerObserver :
        ObjectReference<Disposable?>(null),
        ObservableObserver<R>,
        ValueCallback<R> by callbacks,
        ErrorCallback by callbacks {

        override fun onSubscribe(disposable: Disposable) {
            value = disposable
            add(disposable)
        }

        override fun onComplete() {
            remove(value!!)

            val oldState =
                state.getAndUpdate {
                    it.copy(
                        queue = it.queue.drop(1),
                        isMappedSourceActive = it.queue.isNotEmpty()
                    )
                }

            if (oldState.queue.isNotEmpty()) {
                mapAndSubscribe(oldState.queue[0])
            } else if (oldState.isUpstreamCompleted) {
                callbacks.onComplete()
            }
        }
    }
}
