package com.badoo.reaktive.scheduler

import java.util.concurrent.TimeUnit

actual fun createIoScheduler(): Scheduler =
    ExecutorServiceScheduler(
        CachedExecutorServiceStrategy(
            keepAliveTimeoutMillis = TimeUnit.MINUTES.toMillis(1L),
            threadFactory = ThreadFactoryImpl("IO")
        )
    )