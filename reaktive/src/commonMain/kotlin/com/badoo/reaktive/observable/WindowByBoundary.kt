package com.badoo.reaktive.observable

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.delay
import com.badoo.reaktive.single.repeat
import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer

/**
 * Returns an [Observable] that emits non-overlapping windows of elements it collects from the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-long-boolean-).
 */
fun <T> Observable<T>.window(
    spanMillis: Long,
    scheduler: Scheduler,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(spanMillis > 0) { "spanMillis must by positive" }

    return window(
        boundaries = singleOf(Unit).delay(spanMillis, scheduler).repeat(),
        limit = limit,
        restartOnLimit = restartOnLimit
    )
}

/**
 * Returns an [Observable] that emits non-overlapping windows of elements it collects from the source [Observable].
 * Window boundaries are determined by the elements emitted by the specified [boundaries][boundaries] [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-io.reactivex.ObservableSource-int-).
 */
fun <T> Observable<T>.window(
    boundaries: Observable<*>,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(limit > 0) { "Limit must be positive" }

    return observable { emitter ->
        WindowByBoundary(
            upstream = this,
            boundaries = boundaries,
            limit = limit,
            restartOnLimit = restartOnLimit,
            emitter = emitter
        )
    }
}

private class WindowByBoundary<T>(
    upstream: Observable<T>,
    boundaries: Observable<*>,
    private val limit: Long,
    private val restartOnLimit: Boolean,
    private val emitter: ObservableEmitter<Observable<T>>
) {
    private val window: AtomicReference<UnicastSubject<T>?>
    private val actor = serializer(onValue = ::processEvent)
    private val upstreamObserver = UpstreamObserver<T>(actor)
    private val boundariesObserver = BoundaryObserver(actor)
    private val valueCount = AtomicLong()

    init {
        val firstWindow = UnicastSubject<T>()
        window = AtomicReference(firstWindow)
        startWindow(firstWindow)
        emitter.setCancellable { actor.accept(Event.DownstreamDisposed) }
        upstream.subscribe(upstreamObserver)
        boundaries.subscribe(boundariesObserver)
    }

    @Suppress("UNCHECKED_CAST")
    private fun processEvent(event: Any?): Boolean =
        if (event is Event<*>) onEvent(event as Event<T>) else onValue(event as T)

    private fun onEvent(event: Event<T>): Boolean =
        when (event) {
            Event.Boundary -> onBoundary()
            Event.Completed -> onCompleted()
            is Event.WindowDisposed -> onWindowDisposed(event.subject)
            Event.DownstreamDisposed -> onDownstreamDisposed()
            is Event.Error -> onError(event.error)
        }

    private fun onBoundary(): Boolean {
        replaceWindow(UnicastSubject(), UnicastSubject<*>::onComplete)

        return true
    }

    private fun onCompleted(): Boolean {
        replaceWindow(null, UnicastSubject<*>::onComplete)
        emitter.onComplete()
        upstreamObserver.dispose()
        boundariesObserver.dispose()

        return false
    }

    private fun onError(error: Throwable): Boolean {
        replaceWindow(null) { it.onError(error) }
        emitter.onError(error)
        upstreamObserver.dispose()
        boundariesObserver.dispose()

        return false
    }

    private fun onWindowDisposed(subject: UnicastSubject<T>): Boolean {
        if (window.compareAndSet(subject, null) && boundariesObserver.isDisposed) {
            upstreamObserver.dispose()
        }

        return true
    }

    private fun onDownstreamDisposed(): Boolean {
        boundariesObserver.dispose()

        if (window.value == null) {
            upstreamObserver.dispose()
        }

        return true
    }

    private fun onValue(value: T): Boolean {
        val windowSubject = window.value ?: return true

        windowSubject.onNext(value)

        val newCount = valueCount.addAndGet(1)
        if (newCount == limit) {
            replaceWindow(if (restartOnLimit) UnicastSubject() else null, UnicastSubject<*>::onComplete)
        }

        return true
    }

    private inline fun replaceWindow(newWindow: UnicastSubject<T>?, finishWindow: (UnicastSubject<T>) -> Unit) {
        window.getAndSet(newWindow)?.also(finishWindow)

        if (newWindow != null) {
            startWindow(newWindow)
        }
    }

    private fun startWindow(window: UnicastSubject<T>) {
        valueCount.value = 0
        val windowWrapper = WindowWrapper(window.doOnBeforeDispose { actor.accept(Event.WindowDisposed(window)) })
        emitter.onNext(windowWrapper)

        if (!windowWrapper.isSubscribed.value) {
            replaceWindow(null, UnicastSubject<*>::onComplete)
        }
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

    private class UpstreamObserver<in T>(
        private val actor: Serializer<Any?>
    ) : ObservableObserver<T>, SerialDisposable() {
        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onNext(value: T) {
            actor.accept(value)
        }

        override fun onComplete() {
            actor.accept(Event.Completed)
        }

        override fun onError(error: Throwable) {
            actor.accept(Event.Error(error))
        }
    }

    private class BoundaryObserver(
        private val actor: Serializer<Any?>
    ) : ObservableObserver<Any?>, SerialDisposable() {
        override fun onSubscribe(disposable: Disposable) {
            set(disposable)
        }

        override fun onNext(value: Any?) {
            actor.accept(Event.Boundary)
        }

        override fun onComplete() {
            actor.accept(Event.Completed)
        }

        override fun onError(error: Throwable) {
            actor.accept(Event.Error(error))
        }
    }

    private sealed class Event<out T> {
        object Boundary : Event<Nothing>()
        object Completed : Event<Nothing>()
        class WindowDisposed<T>(val subject: UnicastSubject<T>) : Event<T>()
        object DownstreamDisposed : Event<Nothing>()
        class Error(val error: Throwable) : Event<Nothing>()
    }
}
