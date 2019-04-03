package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

fun Completable.subscribeOn(scheduler: Scheduler): Completable =
    completable { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        executor.submit {
            subscribeSafe(
                object : CompletableObserver by observer {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }
                }
            )
        }
    }