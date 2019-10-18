package com.badoo.reaktive.looperthread

import com.badoo.reaktive.utils.ExpirationPool

internal class CachedLooperThreadStrategy(
    private val keepAliveTimeoutMillis: Long
) : LooperThreadStrategy {

    override fun get(): LooperThread = pool.acquire() ?: LooperThread()

    override fun recycle(looperThread: LooperThread) {
        pool.release(looperThread, keepAliveTimeoutMillis)
    }

    override fun destroy() {
        // no-op
    }

    private companion object {
        private val pool by lazy { ExpirationPool(LooperThread::destroy) }
    }
}
