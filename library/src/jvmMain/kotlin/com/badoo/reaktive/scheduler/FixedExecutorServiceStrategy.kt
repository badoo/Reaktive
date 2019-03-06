package com.badoo.reaktive.scheduler

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

internal class FixedExecutorServiceStrategy(
    threadCount: Int,
    threadFactory: ThreadFactory
) : ExecutorServiceStrategy {

    private val pool =
        List<Lazy<ScheduledExecutorService>>(threadCount) {
            lazy { Executors.newSingleThreadScheduledExecutor(threadFactory) }
        }

    private val executorIndex = AtomicInteger()

    override fun get(): ScheduledExecutorService =
        pool[executorIndex.getAndIncrement() % pool.size].value

    override fun recycle(executorService: ScheduledExecutorService) {
        // no-op
    }
}