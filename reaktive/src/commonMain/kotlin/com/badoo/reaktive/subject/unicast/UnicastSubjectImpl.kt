package com.badoo.reaktive.subject.unicast

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.AbstractSubject
import com.badoo.reaktive.subject.LinkedQueue
import com.badoo.reaktive.subject.LinkedQueue.Node
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.forEachAndGetLast
import com.badoo.reaktive.subject.isActive
import com.badoo.reaktive.utils.atomic.AtomicReference

internal class UnicastSubjectImpl<T>(
    bufferLimit: Int,
    private val onTerminate: () -> Unit,
) : AbstractSubject<T, Node<T>?>(), UnicastSubject<T> {

    private val buffer = AtomicReference<LinkedQueue<T>?>(LinkedQueue(limit = bufferLimit))

    override fun subscribe(observer: ObservableObserver<T>) {
        val disposable = observer.onSubscribe() ?: return
        val buffer = buffer.getAndSet(null)

        if (buffer != null) {
            val lastNode = buffer.head?.forEachAndGetLast { observer.onNext(it) }
            onSubscribe(observer, disposable, lastNode)
        } else {
            observer.onError(IllegalStateException("Only one single observer allowed for UnicastSubject"))
        }
    }

    override fun onAfterSubscribe(observer: ObservableObserver<T>, token: Node<T>?) {
        super.onAfterSubscribe(observer, token)

        token?.next?.forEachAndGetLast {
            observer.onNext(it)
        }
    }

    override fun onBeforeNext(value: T) {
        super.onBeforeNext(value)

        buffer.value?.addLast(value)
    }

    override fun onAfterUnsubscribe(observer: ObservableObserver<T>) {
        super.onAfterUnsubscribe(observer)

        if (isActive) {
            status = Subject.Status.Completed
        }
    }

    override fun onStatusChanged(status: Subject.Status) {
        super.onStatusChanged(status)

        if (!isActive) {
            onTerminate()
        }
    }
}
