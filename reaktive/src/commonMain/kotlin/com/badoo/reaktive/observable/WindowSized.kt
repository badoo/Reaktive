package com.badoo.reaktive.observable

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Returns an [Observable] that emits windows of elements it collects from the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-long-).
 */
fun <T> Observable<T>.window(
    count: Long,
    skip: Long = count
): Observable<Observable<T>> {
    require(count > 0) { "count > 0 required but it was $count" }
    require(skip > 0) { "skip > 0 required but it was $skip" }

    return observable { emitter ->
        val activeWindowsCount = AtomicInt(1)
        val upstreamObserver = UpstreamObserver(
            count = count,
            skip = skip,
            activeWindowsCount = activeWindowsCount,
            downstream = emitter
        )

        emitter.setCancellable {
            if (activeWindowsCount.addAndGet(-1) == 0) {
                upstreamObserver.dispose()
            }
        }

        subscribe(upstreamObserver)
    }
}

private class UpstreamObserver<T>(
    private val count: Long,
    private val skip: Long,
    private val activeWindowsCount: AtomicInt,
    private val downstream: ObservableCallbacks<Observable<T>>
) : SerialDisposable(), ObservableObserver<T> {
    private val windows = SharedQueue<UnicastSubject<T>>()
    private val skippedCount = AtomicLong()
    private val tailWindowValuesCount = AtomicLong()
    private val onWindowTerminate: () -> Unit = {
        if (activeWindowsCount.addAndGet(-1) == 0) {
            dispose()
        }
    }

    override fun onSubscribe(disposable: Disposable) {
        set(disposable)
    }

    override fun onNext(value: T) {
        val skipped = skippedCount.value
        val windowWrapper: WindowWrapper<T>?

        if (skipped == 0L) {
            activeWindowsCount.addAndGet(1)
            val window = UnicastSubject<T>(onTerminate = onWindowTerminate)
            windowWrapper = WindowWrapper(window)
            windows.offer(window)
            downstream.onNext(windowWrapper)
        } else {
            windowWrapper = null
        }

        windows.forEach { it.onNext(value) }

        skippedCount.value = (skipped + 1) % skip

        if (tailWindowValuesCount.value + 1 == count) {
            requireNotNull(windows.poll()).onComplete()
            tailWindowValuesCount.addAndGet(1 - skip)
        } else {
            tailWindowValuesCount.addAndGet(1)
        }

        if (windowWrapper?.isSubscribed?.value == false) {
            windowWrapper.window.onComplete()
        }
    }

    override fun onComplete() {
        windows.forEach { it.onComplete() }
        downstream.onComplete()
        dispose()
    }

    override fun onError(error: Throwable) {
        windows.forEach { it.onError(error) }
        downstream.onError(error)
        dispose()
    }

    private class WindowWrapper<T>(
        val window: UnicastSubject<T>
    ) : Observable<T> {
        val isSubscribed = AtomicBoolean()

        override fun subscribe(observer: ObservableObserver<T>) {
            isSubscribed.value = true
            window.subscribe(observer)
        }
    }
}
