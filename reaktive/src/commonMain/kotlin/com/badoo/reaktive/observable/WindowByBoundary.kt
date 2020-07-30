package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableOfNever
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.serializer.serializer

/**
 * Returns an `Observable` that emits windows of items it collects from the upstream `Observable`.
 * A new window is opened every time when the `boundaries` `Observable` emits a new [WindowBoundary].
 * A window is closed when one of the following events occurs:
 * - its [WindowBoundary.closingSignal] completed;
 * - the window produced [WindowBoundary.limit] amount of values;
 * - the `isExclusive` flag is `true` and a new [WindowBoundary] is emitted by the `boundaries` `Observable`.
 *
 * If the [WindowBoundary.restartOnLimit] is `true` and the [WindowBoundary.limit] is reached then
 * the corresponding window will be restarted and its [WindowBoundary.closingSignal] will be resubscribed.
 *
 * @receiver a source `Observable` of items
 * @param boundaries an `Observable` of [WindowBoundary`] that, when it emits an item, causes another window to be created
 * @param isExclusive when `true` then every emission of the [WindowBoundary] causes any currently active window to be closed
 * @return an `Observable` that emits windows of items emitted by the upstream `Observable` that are controlled by
 * the corresponding [WindowBoundary]
 */
fun <T> Observable<T>.window(boundaries: Observable<WindowBoundary>, isExclusive: Boolean = false): Observable<Observable<T>> =
    observable { emitter ->
        WindowObservableByEmitter(
            upstream = this,
            boundaries = boundaries,
            emitter = emitter,
            isExclusive = isExclusive
        )
    }

class WindowBoundary(
    val closingSignal: Completable = completableOfNever(),
    val limit: Long = Long.MAX_VALUE,
    val restartOnLimit: Boolean = false
)

private class WindowObservableByEmitter<in T>(
    upstream: Observable<T>,
    boundaries: Observable<WindowBoundary>,
    private val emitter: ObservableEmitter<Observable<T>>,
    private val isExclusive: Boolean
) {

    private val disposables = CompositeDisposable()
    private val windows = SharedList<WindowDescriptor<T>>()
    private val actor = serializer(::onDataReceived)

    init {
        emitter.setDisposable(disposables)
        boundaries.subscribe(BoundaryObserver())
        upstream.subscribe(UpstreamObserver())
    }

    private fun onDataReceived(data: Any?): Boolean =
        @Suppress("UNCHECKED_CAST")
        when (data) {
            is WindowBoundary -> onStartWindow(data)
            is WindowDescriptor<*> -> onWindowClosed(data as WindowDescriptor<T>)
            is RestartWindow<*> -> onRestartWindow((data as RestartWindow<T>).descriptor)
            is WindowCompleted -> onCompleted()
            is WindowError -> onErrorReceived(data.error)
            else -> onValueReceived(data as T)
        }.let { true }

    private fun onStartWindow(newBoundary: WindowBoundary) {
        val subject = PublishSubject<T>()
        val windowDescriptor = WindowDescriptor(subject, newBoundary)

        if (isExclusive) {
            closeAllWindows()
        }

        windows += windowDescriptor
        emitter.onNext(subject)
        newBoundary.closingSignal.subscribe(BoundaryClosingObserver(windowDescriptor))
    }

    private fun onRestartWindow(windowDescriptor: WindowDescriptor<T>) {
        closeWindow(windowDescriptor)
        if (windows.remove(windowDescriptor)) {
            onStartWindow(windowDescriptor.boundary)
        }
    }

    private fun onWindowClosed(windowDescriptor: WindowDescriptor<T>) {
        windows -= windowDescriptor
        closeWindow(windowDescriptor)
    }

    private fun onCompleted() {
        closeAllWindows()
        emitter.onComplete()
    }

    private fun onValueReceived(value: T) {
        windows.forEach { window ->
            checkWindowLimit(window)
            window.subject.onNext(value)
        }
    }

    private fun checkWindowLimit(window: WindowDescriptor<T>) {
        val limit = window.boundary.limit.takeIf { it < Long.MAX_VALUE } ?: return

        if (window.itemCount.addAndGet(1) == limit) {
            if (window.boundary.restartOnLimit) {
                actor.accept(RestartWindow(window))
            } else {
                actor.accept(window)
            }
        }
    }

    private fun onErrorReceived(error: Throwable) {
        closeAllWindows()
        emitter.onError(error)
    }

    private fun closeAllWindows() {
        windows.forEach(::closeWindow)
        windows.clear()
    }

    private fun closeWindow(windowDescriptor: WindowDescriptor<T>) {
        windowDescriptor.subject.onComplete()
        windowDescriptor.closingSignalDisposableWrapper.dispose()
    }

    private inner class UpstreamObserver : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            disposables += disposable
        }

        override fun onNext(value: T) {
            actor.accept(value)
        }

        override fun onComplete() {
            actor.accept(WindowCompleted)
        }

        override fun onError(error: Throwable) {
            actor.accept(WindowError(error))
        }
    }

    private inner class BoundaryObserver : ObservableObserver<WindowBoundary> {
        override fun onSubscribe(disposable: Disposable) {
            disposables += disposable
        }

        override fun onNext(value: WindowBoundary) {
            if (value.limit > 0) {
                actor.accept(value)
            } else {
                actor.accept(WindowError(IllegalArgumentException("Window limit must be positive")))
            }
        }

        override fun onComplete() {
            actor.accept(WindowCompleted)
        }

        override fun onError(error: Throwable) {
            actor.accept(WindowError(error))
        }
    }

    private inner class BoundaryClosingObserver(
        private val windowDescriptor: WindowDescriptor<T>
    ) : CompletableObserver, ObjectReference<Disposable?>(null) {
        override fun onSubscribe(disposable: Disposable) {
            value = disposable
            disposables += disposable
            windowDescriptor.closingSignalDisposableWrapper.set(disposable)
        }

        override fun onComplete() {
            actor.accept(windowDescriptor)
            disposables -= requireNotNull(value)
        }

        override fun onError(error: Throwable) {
            actor.accept(WindowError(error))
            disposables -= requireNotNull(value)
        }
    }
}

private class WindowDescriptor<T>(
    val subject: Subject<T>,
    val boundary: WindowBoundary
) {
    val closingSignalDisposableWrapper = DisposableWrapper()
    val itemCount: AtomicLong = AtomicLong()
}

private class RestartWindow<T>(val descriptor: WindowDescriptor<T>)

private object WindowCompleted

private class WindowError(val error: Throwable)
