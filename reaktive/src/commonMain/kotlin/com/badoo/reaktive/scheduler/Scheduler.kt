package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable
import kotlin.time.Duration

/**
 * Base interface for schedulers.
 *
 * The following schedulers are provided by the library:
 * - [mainScheduler]
 * - [computationScheduler]
 * - [ioScheduler]
 * - [trampolineScheduler]
 * - [singleScheduler]
 * - [newThreadScheduler]
 */
interface Scheduler {

    /**
     * Creates a new instance of [Executor]. Throws an exception if [Scheduler] is destroyed.
     */
    fun newExecutor(): Executor

    /**
     * Destroys the scheduler
     */
    fun destroy()

    /**
     * Base interface for [Scheduler] executors. All tasks are executed synchronously one by one, never concurrently.
     * [Executor]s must be disposed when they are no longer needed.
     */
    interface Executor : Disposable {

        /**
         * Submits a new task, repeating if [period] is specified.
         *
         * @param delay a delay before first execution, default is [Duration.ZERO] (as soon as possible).
         * @param period a period between executions, default is [Duration.INFINITE] (non-repeating).
         * @param task the task to be executed
         */
        fun submit(
            delay: Duration = Duration.ZERO,
            period: Duration = Duration.INFINITE,
            task: () -> Unit,
        )

        /**
         * Cancels all tasks. All running tasks will be interrupted, all pending tasks will not be executed.
         */
        fun cancel()
    }
}
