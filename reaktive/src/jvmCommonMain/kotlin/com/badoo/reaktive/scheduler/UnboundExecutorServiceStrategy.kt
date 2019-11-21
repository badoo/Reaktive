package com.badoo.reaktive.scheduler

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

internal class UnboundExecutorServiceStrategy(
    private val threadFactory: ThreadFactory
) : ExecutorServiceStrategy {

    override fun get(): ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory)

    override fun recycle(executorService: ScheduledExecutorService) {
        executorService.shutdown()
    }

    override fun destroy() {
        // no-op
    }
}
