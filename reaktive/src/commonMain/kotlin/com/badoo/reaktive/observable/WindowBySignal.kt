package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableTimer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.maybe.maybeTimer
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.repeatWhen
import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer
import kotlin.time.Duration

/**
 * Returns an [Observable] that emits possibly overlapping windows of elements it collects from the source [Observable].
 *
 * Please refer to the corresponding RxJava
 * [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Observable<T>.window(
    span: Duration,
    skip: Duration,
    scheduler: Scheduler,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(span.isPositive()) { "Span duration must by positive" }
    require(skip.isPositive()) { "Skip duration must by positive" }

    return window(
        opening = singleOf(Unit).repeatWhen { _, _ -> maybeTimer(skip, scheduler) },
        closing = { completableTimer(span, scheduler) },
        limit = limit,
        restartOnLimit = restartOnLimit
    )
}

/**
 * Returns an [Observable] that emits possibly overlapping windows of elements it collects from the source [Observable].
 * Every new window is opened when the [opening][opening] [Observable] emits an element.
 * Each window is closed when the corresponding [Observable] returned by the [closing] function completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-io.reactivex.ObservableSource-io.reactivex.functions.Function-).
 */
fun <T, S> Observable<T>.window(
    opening: Observable<S>,
    closing: (S) -> Completable,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(limit > 0) { "Limit must be positive" }

    return observable { emitter ->
        WindowBySignal(
            upstream = this,
            opening = opening,
            closing = closing,
            limit = limit,
            restartOnLimit = restartOnLimit,
            emitter = emitter
        )
    }
}

private class WindowBySignal<T, S>(
    upstream: Observable<T>,
    opening: Observable<S>,
    private val closing: (S) -> Completable,
    private val limit: Long,
    private val restartOnLimit: Boolean,
    private val emitter: ObservableEmitter<Observable<T>>
) {
    private val actor = serializer(onValue = ::processEvent)
    private val upstreamObserver = UpstreamObserver<T>(actor)
    private val openingObserver = OpeningObserver<S>(actor)
    private var windows: Set<ClosingObserver<T>> = HashSet() // Copy-on-write behaviour

    init {
        emitter.setCancellable { actor.accept(Event.DownstreamDisposed) }
        upstream.subscribe(upstreamObserver)
        opening.subscribe(openingObserver)
    }

    @Suppress("UNCHECKED_CAST")
    private fun processEvent(event: Any?): Boolean =
        if (event is Event<*, *>) onEvent(event as Event<T, S>) else onValue(event as T)

    private fun onEvent(event: Event<T, S>): Boolean =
        when (event) {
            is Event.Open -> onOpen(event.value)
            is Event.Close -> onClose(event.window)
            Event.UpstreamCompleted -> onUpstreamCompleted()
            Event.OpeningCompleted -> onOpeningCompleted()
            Event.DownstreamDisposed -> onDownstreamDisposed()
            is Event.Error -> onError(event.error)
        }

    private fun onOpen(value: S): Boolean {
        val closing =
            try {
                closing(value)
            } catch (e: Throwable) {
                onError(e)
                return false
            }

        return open(closing)
    }

    private fun open(closing: Completable): Boolean {
        val window = UnicastSubject<T>()
        val windowWrapper = WindowWrapper(window)

        emitter.onNext(windowWrapper)

        if (windowWrapper.isSubscribed.value) {
            val closingObserver = ClosingObserver(actor, closing, window)
            windows += closingObserver

            try {
                closing.subscribe(closingObserver)
            } catch (e: Throwable) {
                onError(e)
                return false
            }
        } else {
            window.onComplete()
        }

        return true
    }

    private fun onClose(window: ClosingObserver<T>): Boolean {
        windows -= window
        val windowCount = windows.size
        window.subject.onComplete()

        if ((windowCount == 0) && (upstreamObserver.isDone() || openingObserver.isDone())) {
            upstreamObserver.dispose()
            openingObserver.dispose()
            emitter.onComplete()
            return false
        }

        return true
    }

    private fun onUpstreamCompleted(): Boolean {
        upstreamObserver.markCompleted()
        openingObserver.dispose()

        windows
            .also { windows = HashSet() }
            .forEach {
                it.dispose()
                it.subject.onComplete()
            }

        emitter.onComplete()

        return false
    }

    private fun onOpeningCompleted(): Boolean {
        openingObserver.markCompleted()

        if (windows.isEmpty()) {
            upstreamObserver.dispose()
            emitter.onComplete()
            return false
        }

        return true
    }

    private fun onDownstreamDisposed(): Boolean {
        openingObserver.dispose()

        if (windows.isEmpty()) {
            upstreamObserver.dispose()
            return false
        }

        return true
    }

    private fun onError(error: Throwable): Boolean {
        upstreamObserver.dispose()
        openingObserver.dispose()
        emitter.onError(error)

        windows
            .also { windows = HashSet() }
            .forEach {
                it.dispose()
                it.subject.onError(error)
            }

        return false
    }

    private fun onValue(value: T): Boolean {
        windows.forEach {
            it.subject.onNext(value)

            it.count++
            if (it.count == limit) {
                onClose(it)
                if (restartOnLimit) {
                    it.count = 0
                    open(it.closing)
                }
            }
        }

        return true
    }

    private sealed class Event<out T, out S> {
        class Open<out S>(val value: S) : Event<Nothing, S>()
        class Close<T>(val window: ClosingObserver<T>) : Event<T, Nothing>()
        object UpstreamCompleted : Event<Nothing, Nothing>()
        object OpeningCompleted : Event<Nothing, Nothing>()
        object DownstreamDisposed : Event<Nothing, Nothing>()
        class Error(val error: Throwable) : Event<Nothing, Nothing>()
    }

    private class WindowWrapper<out T>(
        private val delegate: Observable<T>
    ) : Observable<T> {
        val isSubscribed = AtomicBoolean()

        override fun subscribe(observer: ObservableObserver<T>) {
            isSubscribed.value = true
            delegate.subscribe(observer)
        }
    }

    private abstract class AbstractObserver(
        private val actor: Serializer<Any?>
    ) : Observer, ErrorCallback, SerialDisposable() {
        private var isCompleted = AtomicBoolean()

        fun markCompleted() {
            isCompleted.value = true
        }

        fun isDone(): Boolean = isCompleted.value || isDisposed

        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onError(error: Throwable) {
            actor.accept(Event.Error(error))
        }
    }

    private class UpstreamObserver<in T>(
        private val actor: Serializer<Any?>
    ) : AbstractObserver(actor), ObservableObserver<T> {
        override fun onNext(value: T) {
            actor.accept(value)
        }

        override fun onComplete() {
            actor.accept(Event.UpstreamCompleted)
        }
    }

    private class OpeningObserver<in S>(
        private val actor: Serializer<Any?>
    ) : AbstractObserver(actor), ObservableObserver<S> {
        override fun onNext(value: S) {
            actor.accept(Event.Open(value))
        }

        override fun onComplete() {
            actor.accept(Event.OpeningCompleted)
        }
    }

    private class ClosingObserver<T>(
        private val actor: Serializer<Any?>,
        val closing: Completable,
        val subject: UnicastSubject<T>
    ) : AbstractObserver(actor), CompletableObserver {
        var count: Long = 0L

        override fun onComplete() {
            actor.accept(Event.Close(this))
        }
    }
}
