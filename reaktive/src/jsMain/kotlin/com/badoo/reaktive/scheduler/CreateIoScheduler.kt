package com.badoo.reaktive.scheduler

actual fun createIoScheduler(): Scheduler =
    throw NotImplementedError("IO scheduler is not supported in JS target")