package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.coroutinesinterop.test.CoroutineCancellationVerifier
import com.badoo.reaktive.coroutinesinterop.test.verifyCancellation
import com.badoo.reaktive.maybe.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

class MaybeFromCoroutineNativeTest {

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        val cancellationVerifier = CoroutineCancellationVerifier()

        val observer =
            maybeFromCoroutine { cancellationVerifier.suspendCancellable() }
                .subscribeOn(ioScheduler)
                .test()

        cancellationVerifier.verifyCancellation(observer)
    }
}