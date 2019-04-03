package com.badoo.reaktive.scheduler

/**
 * Provides the global instance of Main [Scheduler]
 */
val mainScheduler: Scheduler by lazy { mainSchedulerFactory() }

/**
 * Provides the global instance of Computation [Scheduler]
 */
val computationScheduler: Scheduler by lazy { computationSchedulerFactory() }

/**
 * Provides the global instance of IO [Scheduler]
 */
val ioScheduler: Scheduler by lazy { ioSchedulerFactory() }

/**
 * Provides the global instance of Trampoline [Scheduler]
 */
val trampolineScheduler: Scheduler by lazy { trampolineSchedulerFactory() }

private var mainSchedulerFactory: () -> Scheduler = ::createMainScheduler
private var computationSchedulerFactory: () -> Scheduler = ::createComputationScheduler
private var ioSchedulerFactory: () -> Scheduler = ::createIoScheduler
private var trampolineSchedulerFactory: () -> Scheduler = ::createTrampolineScheduler

/**
 * Creates a new instance of Main [Scheduler]
 */
expect fun createMainScheduler(): Scheduler

/**
 * Creates a new instance of IO [Scheduler]
 */
expect fun createIoScheduler(): Scheduler

/**
 * Creates a new instance of Computation [Scheduler]
 */
expect fun createComputationScheduler(): Scheduler

/**
 * Creates a new instance of Trampoline [Scheduler]
 */
expect fun createTrampolineScheduler(): Scheduler

/**
 * Overrides [Scheduler]s if they were not created yet
 *
 * @param main a factory for Main [Scheduler], if not set then default factory will be used
 * @param computation a factory for Computation [Scheduler], if not set then default factory will be used
 * @param io a factory for IO [Scheduler], if not set then default factory will be used
 * @param trampoline a factory for Trampoline [Scheduler], if not set then default factory will be used
 */
fun overrideSchedulers(
    main: () -> Scheduler = ::createMainScheduler,
    computation: () -> Scheduler = ::createComputationScheduler,
    io: () -> Scheduler = ::createIoScheduler,
    trampoline: () -> Scheduler = ::createTrampolineScheduler
) {
    mainSchedulerFactory = main
    computationSchedulerFactory = computation
    ioSchedulerFactory = io
    trampolineSchedulerFactory = trampoline
}