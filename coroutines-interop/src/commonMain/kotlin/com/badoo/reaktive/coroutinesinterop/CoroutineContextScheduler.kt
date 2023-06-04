package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndChange
import com.badoo.reaktive.utils.clock.Clock
import com.badoo.reaktive.utils.coerceAtLeastZero
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

internal class CoroutineContextScheduler(
    private val context: CoroutineContext,
    private val clock: Clock
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(context, clock, disposables)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl(
        private val context: CoroutineContext,
        private val clock: Clock,
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {

        private val mutex = Mutex()
        private val scopeRef = AtomicReference<CoroutineScope?>(newWorkerScope())

        override val isDisposed: Boolean get() = scopeRef.value == null

        init {
            disposables += this
        }

        override fun cancel() {
            scopeRef
                .getAndChange { if (it != null) newWorkerScope() else null }
                ?.cancel()
        }

        override fun dispose() {
            scopeRef
                .getAndSet(null)
                ?.cancel()

            disposables -= this
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            scopeRef.value?.launch {
                execute(
                    Task(
                        startTime = clock.uptime + delay.coerceAtLeastZero(),
                        period = period.coerceAtLeastZero(),
                        task = task,
                    )
                )
            }
        }

        private suspend fun execute(task: Task) {
            if (clock.uptime < task.startTime) {
                coroutineScope {
                    launch {
                        delayUntilStart(task.startTime)
                        executeAndRepeatIfNeeded(task.period, task.task)
                    }
                }
            } else {
                executeAndRepeatIfNeeded(task.period, task.task)
            }
        }

        private suspend fun executeAndRepeatIfNeeded(period: Duration, task: () -> Unit) {
            if (period.isInfinite()) {
                mutex.withLock { task() }
                return
            }

            coroutineScope {
                while (isActive) {
                    val nextStartTime = clock.uptime + period
                    mutex.withLock { task() }
                    delayUntilStart(nextStartTime)
                }
            }
        }

        private suspend fun delayUntilStart(startTime: Duration) {
            val uptime = clock.uptime
            if (uptime < startTime) {
                delay(startTime - uptime)
            }
        }

        private fun newWorkerScope(): CoroutineScope =
            CoroutineScope(context + SupervisorJob())

        private data class Task(
            val startTime: Duration,
            val period: Duration,
            val task: () -> Unit
        )
    }
}
