package com.badoo.reaktive.subject

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.serializer.serializer

internal abstract class AbstractSubject<T, S> : Subject<T> {

    private val observers = ArrayList<ObservableObserver<T>>()
    private val serializer = serializer(onValue = ::onSerializedValue)

    private val _status = AtomicReference<Subject.Status>(Subject.Status.Active)
    override var status: Subject.Status
        get() = _status.value
        protected set(value) {
            _status.value = value
            onStatusChanged(value)
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

    protected fun onSubscribe(observer: ObservableObserver<T>, disposable: SerialDisposable, token: S) {
        serializer.accept(Event.OnSubscribe(observer, disposable, token))
    }

    protected fun Observer.onSubscribe(): SerialDisposable? {
        val disposable = SerialDisposable()
        onSubscribe(disposable)

        return disposable.takeUnless(SerialDisposable::isDisposed)
    }

    protected open fun onAfterSubscribe(observer: ObservableObserver<T>, token: S) {
    }

    protected open fun onAfterUnsubscribe(observer: ObservableObserver<T>) {
    }

    protected open fun onBeforeNext(value: T) {
    }

    protected open fun onStatusChanged(status: Subject.Status) {
    }

    private fun onSerializedValue(value: Any?): Boolean {
        if (value is Event<*, *>) {
            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
            val event = value as Event<T, S>
            when (event) {
                is Event.OnSubscribe -> onSerializedSubscribe(event.observer, event.disposable, event.token)
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

    private fun onSerializedSubscribe(observer: ObservableObserver<T>, disposable: SerialDisposable, token: S) {
        if (disposable.isDisposed) {
            return
        }

        disposable.set(Disposable { serializer.accept(Event.OnUnsubscribe(observer)) })

        when (val status = status) {
            Subject.Status.Active -> {
                observers += observer
                onAfterSubscribe(observer, token)
            }

            Subject.Status.Completed -> observer.onComplete()
            is Subject.Status.Error -> observer.onError(status.error)
        }
    }

    private fun onSerializedUnsubscribe(observer: ObservableObserver<T>) {
        observers -= observer
        onAfterUnsubscribe(observer)
    }

    private fun onSerializedNext(value: T) {
        if (isActive) {
            onBeforeNext(value)
            observers.forEach { it.onNext(value) }
        }
    }

    private fun onSerializedComplete() {
        if (isActive) {
            status = Subject.Status.Completed
            observers.forEach(ObservableObserver<*>::onComplete)
        }
    }

    private fun onSerializedError(error: Throwable) {
        if (isActive) {
            status = Subject.Status.Error(error)
            observers.forEach { it.onError(error) }
        }
    }

    private sealed class Event<out T, out S> {
        class OnSubscribe<T, out S>(
            val observer: ObservableObserver<T>,
            val disposable: SerialDisposable,
            val token: S,
        ) : Event<T, S>()

        class OnUnsubscribe<T>(val observer: ObservableObserver<T>) : Event<T, Nothing>()
        object OnComplete : Event<Nothing, Nothing>()
        class OnError(val error: Throwable) : Event<Nothing, Nothing>()
    }
}
