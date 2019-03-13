package com.badoo.reaktive.scheduler

val mainScheduler: Scheduler by lazy { mainSchedulerFactory() }

private var mainSchedulerFactory: () -> Scheduler = ::MainScheduler

fun overrideAndroidSchedulers(main: () -> Scheduler = ::MainScheduler) {
    mainSchedulerFactory = main
}