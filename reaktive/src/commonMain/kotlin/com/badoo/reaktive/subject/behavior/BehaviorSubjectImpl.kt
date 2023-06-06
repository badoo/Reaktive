package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.AbstractSubject
import com.badoo.reaktive.subject.LinkedQueue
import com.badoo.reaktive.subject.LinkedQueue.Node
import com.badoo.reaktive.subject.forEachAndGetLast
import com.badoo.reaktive.subject.isActive

internal class BehaviorSubjectImpl<T>(initialValue: T) : AbstractSubject<T, Node<T>?>(), BehaviorSubject<T> {

    private val buffer = LinkedQueue<T>(limit = 1).apply { addLast(initialValue) }
    private val node get() = requireNotNull(buffer.head)
    override val value: T get() = node.value

    override fun subscribe(observer: ObservableObserver<T>) {
        val disposable = observer.onSubscribe() ?: return
        val lastNode = node.takeIf { isActive }?.forEachAndGetLast { observer.onNext(it) }
        onSubscribe(observer, disposable, lastNode)
    }

    override fun onAfterSubscribe(observer: ObservableObserver<T>, token: Node<T>?) {
        super.onAfterSubscribe(observer, token)

        token?.next?.forEachAndGetLast {
            observer.onNext(it)
        }
    }

    override fun onBeforeNext(value: T) {
        super.onBeforeNext(value)

        buffer.addLast(value)
    }
}
