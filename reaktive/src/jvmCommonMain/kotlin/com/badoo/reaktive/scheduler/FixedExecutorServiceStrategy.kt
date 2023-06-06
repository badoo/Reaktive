package com.badoo.reaktive.scheduler

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

internal class FixedExecutorServiceStrategy(
    threadCount: Int,
    threadFactory: ThreadFactory
) : ExecutorServiceStrategy {

    private var pool: List<Lazy<ScheduledExecutorService>>? =
        List<Lazy<ScheduledExecutorService>>(threadCount) {
            lazy { Executors.newSingleThreadScheduledExecutor(threadFactory) }
        }

    private val monitor = Any()
    private var executorIndex = 0

    override fun get(): ScheduledExecutorService =
        synchronized(monitor) {
            pool
                ?.let { it[executorIndex++ % it.size] }
                ?.value
                ?: error("Scheduler $this is destroyed")
        }

    override fun recycle(executorService: ScheduledExecutorService) {
        // no-op
    }

    override fun destroy() {
        synchronized(monitor) {
            pool
                ?.asSequence()
                ?.filter(Lazy<*>::isInitialized)
                ?.map(Lazy<ScheduledExecutorService>::value)
                ?.forEach(ScheduledExecutorService::shutdown)

            pool = null
        }
    }
}
