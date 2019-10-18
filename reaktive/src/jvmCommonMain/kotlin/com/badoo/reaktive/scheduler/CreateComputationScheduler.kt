package com.badoo.reaktive.scheduler

actual fun createComputationScheduler(): Scheduler =
    ExecutorServiceScheduler(
        FixedExecutorServiceStrategy(
            threadCount = Runtime.getRuntime().availableProcessors(),
            threadFactory = ThreadFactoryImpl("Computation")
        )
    )
