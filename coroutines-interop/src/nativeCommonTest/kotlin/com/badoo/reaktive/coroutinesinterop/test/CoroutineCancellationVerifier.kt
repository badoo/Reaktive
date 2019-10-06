package com.badoo.reaktive.coroutinesinterop.test

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

class CoroutineCancellationVerifier {

    private val isLaunched = AtomicBoolean()
    private val isCancelled = AtomicBoolean()
    private val launchLock = Lock()
    private val launchCondition = launchLock.newCondition()
    private val cancellationLock = Lock()
    private val cancellationCondition = cancellationLock.newCondition()

    suspend fun suspendCancellable() {
        try {
            suspendCancellableCoroutine<Nothing> {
                launchLock.synchronized {
                    isLaunched.value = true
                    launchCondition.signal()
                }
            }
        } catch (e: CancellationException) {
            cancellationLock.synchronized {
                isCancelled.value = true
                cancellationCondition.signal()
            }
        }
    }

    fun awaitSuspension() {
        launchLock.synchronized {
            launchCondition.waitForOrFail(predicate = isLaunched::value)
        }
    }

    fun awaitCancellation() {
        cancellationLock.synchronized {
            cancellationCondition.waitForOrFail(predicate = isCancelled::value)
        }
    }
}