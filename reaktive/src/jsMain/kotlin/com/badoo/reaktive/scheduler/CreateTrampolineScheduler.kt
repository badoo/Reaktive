package com.badoo.reaktive.scheduler

actual fun createTrampolineScheduler(): Scheduler =
    throw NotImplementedError("Trampoline scheduler is not supported in JS target")