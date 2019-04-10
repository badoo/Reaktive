package com.badoo.reaktive.scheduler

actual fun createComputationScheduler(): Scheduler =
    throw NotImplementedError("Computation scheduler is not supported in JS target")
