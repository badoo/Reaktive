package com.badoo.reaktive.scheduler

import java.util.concurrent.TimeUnit

actual fun createComputationScheduler(): Scheduler =
    ExecutorServiceScheduler(
        FixedExecutorServiceStrategy(
            threadCount = Runtime.getRuntime().availableProcessors(),
            threadFactory = ThreadFactoryImpl("Computation")
        )
    )

actual fun createIoScheduler(): Scheduler =
    ExecutorServiceScheduler(
        CachedExecutorServiceStrategy(
            keepAliveTimeoutMillis = TimeUnit.MINUTES.toMillis(1L),
            threadFactory = ThreadFactoryImpl("IO")
        )
    )