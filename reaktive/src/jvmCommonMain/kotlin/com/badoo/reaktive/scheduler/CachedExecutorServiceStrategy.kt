package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.ExpirationPool
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

internal class CachedExecutorServiceStrategy(
    private val keepAliveTimeoutMillis: Long,
    private val threadFactory: ThreadFactory
) : ExecutorServiceStrategy {

    override fun get(): ScheduledExecutorService =
        pool.acquire() ?: Executors.newSingleThreadScheduledExecutor(threadFactory)

    override fun recycle(executorService: ScheduledExecutorService) {
        pool.release(executorService, keepAliveTimeoutMillis)
    }

    override fun destroy() {
        // no-op
    }

    private companion object {
        private val pool by lazy { ExpirationPool(ScheduledExecutorService::shutdown) }
    }
}
