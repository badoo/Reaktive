package com.badoo.reaktive.looperthread

import com.badoo.reaktive.utils.ExpirationPool
import kotlin.time.Duration

internal class CachedLooperThreadStrategy(
    private val keepAliveTimeout: Duration
) : LooperThreadStrategy {

    override fun get(): LooperThread = pool.acquire() ?: LooperThread()

    override fun recycle(looperThread: LooperThread) {
        pool.release(looperThread, keepAliveTimeout)
    }

    override fun destroy() {
        // no-op
    }

    private companion object {
        private val pool by lazy { ExpirationPool(LooperThread::destroy) }
    }
}
