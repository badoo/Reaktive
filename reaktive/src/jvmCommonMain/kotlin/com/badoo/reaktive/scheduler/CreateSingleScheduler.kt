package com.badoo.reaktive.scheduler

actual fun createSingleScheduler(): Scheduler =
    ExecutorServiceScheduler(
        FixedExecutorServiceStrategy(
            threadCount = 1,
            threadFactory = ThreadFactoryImpl("Single")
        )
    )
