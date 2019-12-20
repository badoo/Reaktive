package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.native.concurrent.SharedImmutable

/**
 * Provides the global instance of Main [Scheduler]
 */
val mainScheduler: Scheduler get() = mainSchedulerFactory.value.value

/**
 * Provides the global instance of Computation [Scheduler]
 */
val computationScheduler: Scheduler get() = computationSchedulerFactory.value.value

/**
 * Provides the global instance of IO [Scheduler]
 */
val ioScheduler: Scheduler get() = ioSchedulerFactory.value.value

/**
 * Provides the global instance of Trampoline [Scheduler]
 */
val trampolineScheduler: Scheduler get() = trampolineSchedulerFactory.value.value

/**
 * Provides the global instance of Single [Scheduler]
 */
val singleScheduler: Scheduler get() = singleSchedulerFactory.value.value

/**
 * Provides the global instance of New Thread [Scheduler]
 */
val newThreadScheduler: Scheduler get() = newThreadSchedulerFactory.value.value

@SharedImmutable
private val mainSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createMainScheduler))

@SharedImmutable
private val computationSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createComputationScheduler))

@SharedImmutable
private val ioSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createIoScheduler))

@SharedImmutable
private val trampolineSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createTrampolineScheduler))

@SharedImmutable
private val singleSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createSingleScheduler))

@SharedImmutable
private val newThreadSchedulerFactory: AtomicReference<Lazy<Scheduler>> =
    AtomicReference(lazy(::createNewThreadScheduler))

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
 * Creates a new instance of Trampoline [Scheduler]
 */
expect fun createSingleScheduler(): Scheduler

/**
 * Creates a new instance of New Thread [Scheduler]
 */
expect fun createNewThreadScheduler(): Scheduler

/**
 * Overrides [Scheduler]s if they were not created yet
 *
 * @param main a factory for Main [Scheduler], if not set then default factory will be used
 * @param computation a factory for Computation [Scheduler], if not set then default factory will be used
 * @param io a factory for IO [Scheduler], if not set then default factory will be used
 * @param trampoline a factory for Trampoline [Scheduler], if not set then default factory will be used
 * @param single a factory for Single [Scheduler], if not set then default factory will be used
 * @param newThread a factory for New Thread [Scheduler], if not set then default factory will be used
 */
fun overrideSchedulers(
    main: () -> Scheduler = ::createMainScheduler,
    computation: () -> Scheduler = ::createComputationScheduler,
    io: () -> Scheduler = ::createIoScheduler,
    trampoline: () -> Scheduler = ::createTrampolineScheduler,
    single: () -> Scheduler = ::createSingleScheduler,
    newThread: () -> Scheduler = ::createNewThreadScheduler
) {
    mainSchedulerFactory.value = lazy(main)
    computationSchedulerFactory.value = lazy(computation)
    ioSchedulerFactory.value = lazy(io)
    trampolineSchedulerFactory.value = lazy(trampoline)
    singleSchedulerFactory.value = lazy(single)
    newThreadSchedulerFactory.value = lazy(newThread)
}
