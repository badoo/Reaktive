package com.badoo.reaktive.scheduler

import java.util.concurrent.ScheduledExecutorService

internal interface ExecutorServiceStrategy {

    fun get(): ScheduledExecutorService

    fun recycle(executorService: ScheduledExecutorService)
}