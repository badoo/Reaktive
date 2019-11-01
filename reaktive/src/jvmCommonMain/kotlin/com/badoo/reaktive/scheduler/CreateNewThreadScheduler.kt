package com.badoo.reaktive.scheduler

actual fun createNewThreadScheduler(): Scheduler =
    ExecutorServiceScheduler(
        UnboundExecutorServiceStrategy(
            threadFactory = ThreadFactoryImpl("NewThread")
        )
    )
