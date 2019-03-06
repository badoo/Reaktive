package com.badoo.reaktive.scheduler

import java.util.concurrent.TimeUnit

actual fun computationScheduler(): Scheduler =
    ExecutorServiceScheduler(
        FixedExecutorServiceStrategy(
            threadCount = Runtime.getRuntime().availableProcessors(),
            threadFactory = ThreadFactoryImpl("Computation")
        )
    )

actual fun ioScheduler(): Scheduler =
    ExecutorServiceScheduler(
        CachedExecutorServiceStrategy(
            keepAliveTimeoutMillis = TimeUnit.MINUTES.toMillis(1L),
            threadFactory = ThreadFactoryImpl("IO")
        )
    )