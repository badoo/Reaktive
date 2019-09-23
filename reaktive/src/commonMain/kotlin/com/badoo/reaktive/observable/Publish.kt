package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.observable.connectable.ConnectableObservable
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.getObserver
import com.badoo.reaktive.subject.publish.publishSubject
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

fun <T> Observable<T>.publish(): ConnectableObservable<T> =
    object : ConnectableObservable<T> {
        private val state = AtomicReference<PublishState<T>?>(null)

        override fun connect(onConnect: ((Disposable) -> Unit)?) {
            var oldState: PublishState<T>? = null

            val newState: PublishState.Connected<T> =
                state.updateAndGet {
                    oldState = it
                    it.ensureConnected()
                }

            val disposables = newState.disposables

            onConnect?.invoke(disposables)

            if ((oldState !is PublishState.Connected) && !disposables.isDisposed) {
                this@publish.subscribeSafe(newState.subject.getObserver(disposables::add))
            }
        }

        private fun PublishState<T>?.ensureConnected(): PublishState.Connected<T> =
            when (this) {
                null -> {
                    val subject = publishSubject<T>()
                    PublishState.Connected(subject, createDisposable(subject))
                }

                is PublishState.NotConnected -> PublishState.Connected(subject, createDisposable(subject))
                is PublishState.Connected -> this
            }

        private fun createDisposable(subject: Subject<*>): CompositeDisposable {
            val disposables = CompositeDisposable()

            disposables +=
                disposable {
                    state.update {
                        it?.takeUnless { it.subject === subject }
                    }
                    subject.onComplete()
                }

            return disposables
        }

        override fun subscribe(observer: ObservableObserver<T>) {
            state
                .updateAndGet {
                    it ?: PublishState.NotConnected(subject = publishSubject())
                }
                .subject
                .subscribe(observer)
        }
    }

private sealed class PublishState<T> {
    abstract val subject: Subject<T>

    class NotConnected<T>(override val subject: Subject<T>) : PublishState<T>()
    class Connected<T>(override val subject: Subject<T>, val disposables: CompositeDisposable) : PublishState<T>()
}