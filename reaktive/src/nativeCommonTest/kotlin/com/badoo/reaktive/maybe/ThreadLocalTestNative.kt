package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.AfterTest
import kotlin.test.Test

class ThreadLocalTestNative {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun does_not_freeze_downstream_WHEN_subscribed_to_a_freezing_upstream() {
        val upstream = maybeUnsafe<Unit> { it.freeze() }
        val downstreamObserver = dummyObserver()
        downstreamObserver.ensureNeverFrozen()

        upstream
            .threadLocal()
            .subscribe(downstreamObserver)
    }

    private fun dummyObserver(): MaybeObserver<Unit> =
        object : MaybeObserver<Unit> {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onSuccess(value: Unit) {
            }

            override fun onComplete() {
            }

            override fun onError(error: Throwable) {
            }
        }
}
