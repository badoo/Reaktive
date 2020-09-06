package com.badoo.reaktive.observable

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Please refer to the corresponding RxJava
 * [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-long-).
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
            emitter = emitter
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
    private val emitter: ObservableEmitter<Observable<T>>
) : DisposableWrapper(), ObservableObserver<T> {
    private val windows = SharedQueue<UnicastSubject<T>>()
    private val counter = AtomicLong()
    private val onWindowTerminate: () -> Unit = {
        if (activeWindowsCount.addAndGet(-1) == 0 && emitter.isDisposed) {
            dispose()
        }
    }

    override fun onSubscribe(disposable: Disposable) {
        set(disposable)
    }

    override fun onNext(value: T) {
        val index = counter.value
        val windowWrapper: WindowWrapper<T>?

        if (index % skip == 0L) {
            activeWindowsCount.addAndGet(1)
            val window = UnicastSubject<T>(onTerminate = onWindowTerminate)
            windowWrapper = WindowWrapper(window)
            windows.offer(window)
            emitter.onNext(windowWrapper)
        } else {
            windowWrapper = null
        }

        windows.forEach { it.onNext(value) }

        val openIndex = index - count + 1
        if (openIndex >= 0 && openIndex % skip == 0L) {
            requireNotNull(windows.poll()).onComplete()
        }

        counter.value = index + 1

        if (windowWrapper?.isSubscribed?.value == false) {
            windowWrapper.window.onComplete()
        }
    }

    override fun onComplete() {
        windows.forEach { it.onComplete() }
        emitter.onComplete()
        dispose()
    }

    override fun onError(error: Throwable) {
        windows.forEach { it.onError(error) }
        emitter.onError(error)
        dispose()
    }
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
