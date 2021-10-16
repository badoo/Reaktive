package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableTimer
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer

/**
 * Emits a first element from the source [Observable] and opens a time window specified by [timeoutMillis].
 * Then does not emit any elements from the source [Observable] while the time window is open, and only emits a
 * most recent element when the time window closes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#throttleLatest-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-boolean-)
 */
fun <T> Observable<T>.throttleLatest(timeoutMillis: Long, scheduler: Scheduler, emitLast: Boolean = false): Observable<T> {
    require(timeoutMillis >= 0L) { "Timeout must not be negative" }

    val timeout = completableTimer(timeoutMillis, scheduler)

    return throttleLatest(timeout = { timeout }, emitLast = emitLast)
}

/**
 * Emits a first element from the source [Observable], then calls the [timeout] supplier and
 * and opens a time window defined by a returned [Completable]. Then does not emit any elements from
 * the source [Observable] while the time window is open, and only emits a most recent element when the time window closes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#throttleLatest-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-boolean-)
 */
fun <T> Observable<T>.throttleLatest(timeout: (T) -> Completable, emitLast: Boolean = false): Observable<T> =
    observable { emitter ->
        ThrottleLatest(
            upstream = this,
            timeoutSupplier = timeout,
            emitLast = emitLast,
            emitter = emitter
        )
    }

private class ThrottleLatest<T>(
    upstream: Observable<T>,
    private val timeoutSupplier: (T) -> Completable,
    private val emitLast: Boolean,
    private val emitter: ObservableEmitter<T>
) {

    private val actor = serializer(onValue = ::processEvent)
    private val lastValue = AtomicReference<Any?>(Uninitialized)
    private val isTimeoutActive = AtomicBoolean()
    private val timeoutObserver = TimeoutObserver(actor)

    init {
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val upstreamObserver = UpstreamObserver<T>(actor)
        disposables += upstreamObserver
        disposables += timeoutObserver

        upstream.subscribe(upstreamObserver)
    }

    @Suppress("UNCHECKED_CAST")
    private fun processEvent(event: Any?): Boolean =
        if (event is Event) onEvent(event) else onValue(event as T)

    private fun onEvent(event: Event): Boolean =
        when (event) {
            Event.Timeout -> onTimeout()
            Event.UpstreamCompleted -> onUpstreamCompleted()
            is Event.Error -> onError(event.error)
        }

    private fun onTimeout(): Boolean {
        val value = lastValue.value
        lastValue.value = Uninitialized
        isTimeoutActive.value = false

        @Suppress("UNCHECKED_CAST")
        return (value === Uninitialized) || startTimeout(value as T)
    }

    private fun onUpstreamCompleted(): Boolean {
        val value = lastValue.value
        lastValue.value = Uninitialized
        if (emitLast && (value !== Uninitialized)) {
            @Suppress("UNCHECKED_CAST")
            emitter.onNext(value as T)
        }

        emitter.onComplete()

        return false
    }

    private fun onError(error: Throwable): Boolean {
        emitter.onError(error)

        return false
    }

    private fun onValue(value: T): Boolean =
        if (isTimeoutActive.value) {
            lastValue.value = value
            true
        } else {
            startTimeout(value)
        }

    private fun startTimeout(value: T): Boolean {
        isTimeoutActive.value = true

        emitter.onNext(value)

        try {
            timeoutSupplier(value).subscribe(timeoutObserver)
        } catch (e: Throwable) {
            onError(e)
            return false
        }

        return true
    }

    private sealed class Event {
        object Timeout : Event()
        object UpstreamCompleted : Event()
        class Error(val error: Throwable) : Event()
    }

    private open class AbstractObserver(
        private val actor: Serializer<Any?>
    ) : Observer, ErrorCallback, SerialDisposable() {
        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onError(error: Throwable) {
            actor.accept(Event.Error(error))
        }
    }

    private class UpstreamObserver<T>(
        private val actor: Serializer<Any?>
    ) : ObservableObserver<T>, AbstractObserver(actor) {
        override fun onNext(value: T) {
            actor.accept(value)
        }

        override fun onComplete() {
            actor.accept(Event.UpstreamCompleted)
        }
    }

    private class TimeoutObserver(
        private val actor: Serializer<Any?>
    ) : CompletableObserver, AbstractObserver(actor) {
        override fun onComplete() {
            actor.accept(Event.Timeout)
        }
    }
}

private sealed class ThrottleLatestValue {
    object None : ThrottleLatestValue()
    object Initial : ThrottleLatestValue()
}
