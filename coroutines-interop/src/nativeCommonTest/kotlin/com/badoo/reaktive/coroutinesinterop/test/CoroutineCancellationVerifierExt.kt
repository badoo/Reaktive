package com.badoo.reaktive.coroutinesinterop.test

import com.badoo.reaktive.disposable.Disposable

fun CoroutineCancellationVerifier.verifyCancellation(disposable: Disposable) {
    awaitSuspension()
    disposable.dispose()
    awaitCancellation()
}