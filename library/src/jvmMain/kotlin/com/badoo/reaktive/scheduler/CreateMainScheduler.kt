package com.badoo.reaktive.scheduler

actual fun createMainScheduler(): Scheduler =
    ExecutorServiceScheduler(
        FixedExecutorServiceStrategy(
            threadCount = 1,
            threadFactory = ThreadFactoryImpl("Main")
        )
    )