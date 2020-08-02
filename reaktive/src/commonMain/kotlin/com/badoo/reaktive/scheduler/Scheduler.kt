package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable

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
         * Submits a new tasks for execution
         *
         * @param delayMillis a delayMillis in milliseconds before execution
         * @param task the task to be executed
         */
        fun submit(delayMillis: Long = 0L, task: () -> Unit)

        /**
         * Submits a new task for repeating execution
         *
         * @param startDelayMillis a delay in milliseconds before first execution
         * @param periodMillis a periodMillis in milliseconds between executions
         * @param task the task to be executed
         */
        fun submitRepeating(startDelayMillis: Long = 0L, periodMillis: Long, task: () -> Unit)

        /**
         * Cancels all tasks. All running tasks will be interrupted, all pending tasks will not be executed.
         */
        fun cancel()
    }
}
