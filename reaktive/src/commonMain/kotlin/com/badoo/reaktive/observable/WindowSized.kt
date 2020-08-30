package com.badoo.reaktive.observable

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.subject.isActive
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.queue.SharedQueue
import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer

fun <T> Observable<T>.window(
    count: Long,
    skip: Long = count
): Observable<Observable<T>> {
    require(count > 0) { "count > 0 required but it was $count" }
    require(skip > 0) { "skip > 0 required but it was $skip" }

    return observable { emitter ->
        WindowSized(
            upstream = this,
            count = count,
            skip = skip,
            emitter = emitter
        )
    }
}

private class WindowSized<T>(
    upstream: Observable<T>,
    private val count: Long,
    private val skip: Long,
    private val emitter: ObservableEmitter<Observable<T>>
) {
    private val windows = SharedQueue<UnicastSubject<T>>()
    private val serializer = serializer(onValue = ::onEvent)
    private val upstreamObserver = UpstreamObserver(serializer)
    private val skippedCount = AtomicLong()
    private val tailWindowValuesCount = AtomicLong()

    init {
        emitter.setCancellable { serializer.accept(Event.OnDownstreamDisposed) }
        upstream.subscribe(upstreamObserver)
    }

    private fun onEvent(event: Event<T>) = when (event) {
        is Event.OnNext -> onNext(event.value)
        is Event.OnComplete -> onComplete()
        is Event.OnError -> onError(event.error)
        Event.OnWindowDisposed -> onWindowDisposed()
        Event.OnDownstreamDisposed -> onDownstreamDisposed()
    }

    private fun onNext(value: T): Boolean {
        val window: UnicastSubject<T>?
        val windowSubscribed: AtomicBoolean?
        if (skippedCount.value == 0L) {
            window = UnicastSubject { serializer.accept(Event.OnWindowDisposed) }
            windowSubscribed = AtomicBoolean(false)
            windows.offer(window)
            emitter.onNext(window.doOnAfterSubscribe { windowSubscribed.value = true })
        } else {
            window = null
            windowSubscribed = null
        }

        windows.forEach { it.onNext(value) }

        skippedCount.value = (skippedCount.value + 1) % skip
        tailWindowValuesCount.addAndGet(1)

        if (tailWindowValuesCount.value == count) {
            requireNotNull(windows.poll()).onComplete()
            tailWindowValuesCount.addAndGet(-skip)
        }

        if (window != null && windowSubscribed != null && windowSubscribed.compareAndSet(false, true)) {
            window.onComplete()
        }

        return true
    }

    private fun onComplete(): Boolean {
        windows.forEach { it.onComplete() }
        emitter.onComplete()
        upstreamObserver.dispose()

        return false
    }

    private fun onError(error: Throwable): Boolean {
        windows.forEach { it.onError(error) }
        emitter.onError(error)
        upstreamObserver.dispose()

        return false
    }

    private fun onWindowDisposed(): Boolean {
        if (windows.none(UnicastSubject<T>::isActive) && emitter.isDisposed) {
            upstreamObserver.dispose()
        }

        return true
    }

    private fun onDownstreamDisposed(): Boolean {
        if (windows.none(UnicastSubject<T>::isActive)) {
            upstreamObserver.dispose()
        }

        return true
    }

    private class UpstreamObserver<T>(
        private val serializer: Serializer<Event<T>>
    ) : ObservableObserver<T>, DisposableWrapper() {

        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onNext(value: T) {
            serializer.accept(Event.OnNext(value))
        }

        override fun onComplete() {
            serializer.accept(Event.OnComplete)
        }

        override fun onError(error: Throwable) {
            serializer.accept(Event.OnError(error))
        }
    }

    private sealed class Event<out T> {
        class OnNext<T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        class OnError(val error: Throwable) : Event<Nothing>()
        object OnWindowDisposed : Event<Nothing>()
        object OnDownstreamDisposed : Event<Nothing>()
    }
}
