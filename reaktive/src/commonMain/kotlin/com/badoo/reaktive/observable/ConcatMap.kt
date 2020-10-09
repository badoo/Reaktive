package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.queue.SharedQueue
import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer

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

    private val actor = serializer(::processEvent)
    private val innerObserver = InnerObserver(callbacks, actor).addTo(this)
    private val queue = SharedQueue<T>()
    private val state = AtomicReference(State.IDLE)

    override fun onSubscribe(disposable: Disposable) {
        add(disposable)
    }

    override fun onNext(value: T) {
        actor.accept(value)
    }

    override fun onComplete() {
        actor.accept(Event.UPSTREAM_COMPLETED)
    }

    @Suppress("UNCHECKED_CAST")
    private fun processEvent(event: Any?): Boolean =
        when (event) {
            Event.UPSTREAM_COMPLETED -> onUpstreamCompleted()
            Event.INNER_COMPLETED -> onInnerCompleted()
            else -> onUpstreamValue(event as T)
        }

    private fun onUpstreamCompleted(): Boolean {
        val oldState = state.value
        state.value = State.UPSTREAM_COMPLETED

        if (oldState == State.IDLE) {
            callbacks.onComplete()
            return false
        }

        return true
    }

    private fun onInnerCompleted(): Boolean {
        if (queue.isEmpty) {
            if (state.value == State.UPSTREAM_COMPLETED) {
                callbacks.onComplete()
                return false
            }

            state.value = State.IDLE
        } else {
            @Suppress("UNCHECKED_CAST")
            subscribe(queue.poll() as T)
        }

        return true
    }

    private fun onUpstreamValue(value: T): Boolean {
        if (state.value == State.INNER_ACTIVE) {
            queue.offer(value)
        } else {
            state.value = State.INNER_ACTIVE
            subscribe(value)
        }

        return true
    }

    private fun subscribe(value: T) {
        tryCatch {
            mapper(value).subscribe(innerObserver)
        }
    }

    private class InnerObserver<R>(
        private val callbacks: ObservableCallbacks<R>,
        private val actor: Serializer<Any?>
    ) : ObservableObserver<R>, DisposableWrapper(), ValueCallback<R> by callbacks, ErrorCallback by callbacks {
        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onComplete() {
            actor.accept(Event.INNER_COMPLETED)
        }
    }

    private enum class State {
        IDLE, INNER_ACTIVE, UPSTREAM_COMPLETED
    }

    private enum class Event {
        UPSTREAM_COMPLETED, INNER_COMPLETED
    }
}
