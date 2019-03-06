package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for schedulers
 */
interface Scheduler {

    /**
     * Creates a new instance of [Executor]
     */
    fun newExecutor(): Executor

    /**
     * Base interface for [Scheduler] executors. All tasks are executed synchronously one by one, never concurrently.
     * [Executor]s must be disposed when they are no longer needed.
     */
    interface Executor : Disposable {

        /**
         * Submits a new tasks for execution
         *
         * @param delay a delay in milliseconds before execution
         * @param task the task to be executed
         */
        fun submit(delay: Long = 0L, task: () -> Unit)

        /**
         * Submits a new task for repeating execution
         *
         * @param startDelay a delay in milliseconds before first execution
         * @param period a period in milliseconds between executions
         * @param task the task to be executed
         */
        fun submitRepeating(startDelay: Long = 0L, period: Long, task: () -> Unit)

        /**
         * Cancels all tasks, all running tasks will be interrupted, all pending tasks will not be executed
         */
        fun cancel()
    }
}