package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler

fun <T> Observable<T>.subscribeOn(scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        executor.submit {
            subscribeSafe(
                object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }
                }
            )
        }
    }
