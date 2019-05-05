package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.native.concurrent.SharedImmutable

/**
 * Provides the global instance of Main [Scheduler]
 */
val mainScheduler: Scheduler get() = mainSchedulerFactory.value()

/**
 * Provides the global instance of Computation [Scheduler]
 */
val computationScheduler: Scheduler get() = computationSchedulerFactory.value()

/**
 * Provides the global instance of IO [Scheduler]
 */
val ioScheduler: Scheduler get() = ioSchedulerFactory.value()

/**
 * Provides the global instance of Trampoline [Scheduler]
 */
val trampolineScheduler: Scheduler get() = trampolineSchedulerFactory.value()

@SharedImmutable
private val mainSchedulerFactory: AtomicReference<() -> Scheduler> = AtomicReference(::createMainScheduler, true)

@SharedImmutable
private val computationSchedulerFactory: AtomicReference<() -> Scheduler> = AtomicReference(::createComputationScheduler, true)

@SharedImmutable
private val ioSchedulerFactory: AtomicReference<() -> Scheduler> = AtomicReference(::createIoScheduler, true)

@SharedImmutable
private val trampolineSchedulerFactory: AtomicReference<() -> Scheduler> = AtomicReference(::createTrampolineScheduler, true)

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
    mainSchedulerFactory.value = main
    computationSchedulerFactory.value = computation
    ioSchedulerFactory.value = io
    trampolineSchedulerFactory.value = trampoline
}