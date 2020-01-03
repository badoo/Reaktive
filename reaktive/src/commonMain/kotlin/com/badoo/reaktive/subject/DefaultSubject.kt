package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.serializer.serializer

internal open class DefaultSubject<T> : Subject<T> {

    private val observers = SharedList<ObservableObserver<T>>()
    private val serializer = serializer(onValue = ::onSerializedValue)
    private val _status = AtomicReference<Subject.Status>(Subject.Status.Active)
    override val status: Subject.Status get() = _status.value

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

    protected open fun onSubscribed(observer: ObservableObserver<T>) {
    }

    protected open fun onAfterSubscribe(observer: ObservableObserver<T>) {
    }

    protected open fun onBeforeNext(value: T) {
    }

    private fun onSerializedValue(value: Any?): Boolean {
        if (value is Event<*>) {
            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
            val event = value as Event<T>
            when (event) {
                is Event.OnSubscribe -> onSerializedSubscribe(event.observer)
                is Event.OnUnsubscribe -> onSerializedUnsubscribe(event.observer)
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
        val disposable = Disposable { serializer.accept(Event.OnUnsubscribe(observer)) }

        observer.onSubscribe(disposable)

        if (disposable.isDisposed) {
            return
        }

        onSubscribed(observer)

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

                is Subject.Status.Active -> {
                }
            }
        }

        observers += observer

        onAfterSubscribe(observer)
    }

    private fun onSerializedUnsubscribe(observer: ObservableObserver<T>) {
        observers -= observer
    }

    private fun onSerializedNext(value: T) {
        if (isActive) {
            onBeforeNext(value)
            observers.forEach { it.onNext(value) }
        }
    }

    private fun onSerializedComplete() {
        if (isActive) {
            _status.value = Subject.Status.Completed
            observers.forEach(ObservableObserver<*>::onComplete)
        }
    }

    private fun onSerializedError(error: Throwable) {
        if (isActive) {
            _status.value = Subject.Status.Error(error)
            observers.forEach { it.onError(error) }
        }
    }

    private sealed class Event<out T> {
        class OnSubscribe<T>(val observer: ObservableObserver<T>) : Event<T>()
        class OnUnsubscribe<T>(val observer: ObservableObserver<T>) : Event<T>()
        object OnComplete : Event<Nothing>()
        class OnError(val error: Throwable) : Event<Nothing>()
    }
}
