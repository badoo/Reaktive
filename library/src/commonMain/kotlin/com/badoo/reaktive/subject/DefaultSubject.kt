package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.serializer.serializer
import com.badoo.reaktive.utils.synchronizedReadWriteProperty

internal open class DefaultSubject<T> : Subject<T> {

    private val lock: Lock = newLock()
    private var observers: Set<ObservableObserver<T>> = emptySet()
    private val serializer = serializer(::onSerializedValue)
    override var status: Subject.Status by synchronizedReadWriteProperty(Subject.Status.Active, lock)

    override fun subscribe(observer: ObservableObserver<T>) {
        serializer.accept(Event.OnSubscribe(observer))
    }

    override fun onNext(value: T) {
        serializer.accept(value)
    }

    override fun onComplete() {
        serializer.accept(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        serializer.accept(Event.OnError(error))
    }

    open fun onAfterSubscribe(observer: ObservableObserver<T>) {
    }

    open fun onBeforeNext(value: T) {
    }

    private fun onSerializedValue(value: Any?): Boolean {
        if (value is Event<*>) {
            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
            val event = value as Event<T>
            when (event) {
                is Event.OnSubscribe -> onSerializedSubscribe(event.observer)
                is Event.OnComplete -> onSerializedComplete()
                is Event.OnError -> onSerializedError(event.error)
            }
        } else {
            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
            onSerializedNext(value as T)
        }

        return true
    }

    private fun onSerializedSubscribe(observer: ObservableObserver<T>) {
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        if (disposableWrapper.isDisposed) {
            return
        }

        status.also {
            when (it) {
                is Subject.Status.Completed -> {
                    observer.onComplete()
                    return
                }

                is Subject.Status.Error -> {
                    observer.onError(it.error)
                    return
                }
            }
        }

        val disposable =
            disposable {
                lock.synchronized {
                    observers = observers - observer
                }
            }

        lock.synchronized {
            observers = observers + observer
        }

        disposableWrapper.set(disposable)
        onAfterSubscribe(observer)
    }

    private fun onSerializedNext(value: T) {
        onBeforeNext(value)

        lock
            .synchronized { observers }
            .forEach { it.onNext(value) }
    }

    private fun onSerializedComplete() {
        if (status is Subject.Status.Active) {
            status = Subject.Status.Completed

            lock
                .synchronized { observers }
                .forEach(ObservableObserver<*>::onComplete)
        }
    }

    private fun onSerializedError(error: Throwable) {
        if (status is Subject.Status.Active) {
            status = Subject.Status.Error(error)

            lock
                .synchronized { observers }
                .forEach { it.onError(error) }
        }
    }

    private sealed class Event<out T> {
        class OnSubscribe<T>(val observer: ObservableObserver<T>) : Event<T>()
        object OnComplete : Event<Nothing>()
        class OnError(val error: Throwable) : Event<Nothing>()
    }
}