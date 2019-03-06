package com.badoo.reaktive.scheduler

/**
 * Provides the global instance of Computation [Scheduler]
 */
val computationScheduler: Scheduler by lazy { computationSchedulerFactory() }

/**
 * Provides the global instance of IO [Scheduler]
 */
val ioScheduler: Scheduler by lazy { ioSchedulerFactory() }

private var computationSchedulerFactory: () -> Scheduler = { computationScheduler() }
private var ioSchedulerFactory: () -> Scheduler = { ioScheduler() }

/**
 * Creates a new instance of IO [Scheduler]
 */
expect fun ioScheduler(): Scheduler

/**
 * Creates a new instance of Computation [Scheduler]
 */
expect fun computationScheduler(): Scheduler

/**
 * Overrides [Scheduler]s if they were not created yet
 *
 * @param computation a factory for Computation [Scheduler], if not set then default factory will be used
 * @param io a factory for IO [Scheduler], if not set then default factory will be used
 */
fun overrideSchedulers(
    computation: () -> Scheduler = { computationScheduler() },
    io: () -> Scheduler = { ioScheduler() }
) {
    computationSchedulerFactory = computation
    ioSchedulerFactory = io
}