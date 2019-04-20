package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

fun <T> Maybe<T>.subscribeOn(scheduler: Scheduler): Maybe<T> =
    maybeUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        executor.submit {
            subscribeSafe(
                object : MaybeObserver<T> by observer {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }
                }
            )
        }
    }