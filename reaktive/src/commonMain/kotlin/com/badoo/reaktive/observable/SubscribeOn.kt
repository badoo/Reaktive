package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

fun <T> Observable<T>.subscribeOn(scheduler: Scheduler): Observable<T> =
    observable { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        executor.submit {
            subscribeSafe(
                object : ObservableObserver<T> by observer {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }
                }
            )
        }
    }