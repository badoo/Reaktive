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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

@ExperimentalCoroutinesApi // Channels are experimental
internal class CoroutineContextScheduler(
    private val context: CoroutineContext,
    private val clock: Clock
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(context, clock, disposables)

    override fun destroy() {
        disposables.dispose()
    }

    @OptIn(ObsoleteCoroutinesApi::class) // Replace channels with SharedFlow
    private class ExecutorImpl(
        context: CoroutineContext,
        private val clock: Clock,
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {

        private val channelRef = AtomicReference<ChannelHolder?>(ChannelHolder())
        private val mutex = Mutex()
        override val isDisposed: Boolean get() = !job.isActive

        private val job =
            GlobalScope.launch(context) {
                while (true) {
                    val receiveChannel = channelRef.value?.receiveChannel ?: break
                    try {
                        val iterator = receiveChannel.iterator()
                        while (!receiveChannel.isClosedForReceive && iterator.hasNext()) {
                            execute(this, iterator.next())
                        }
                    } finally {
                        receiveChannel.cancel()
                    }
                }
            }

        init {
            disposables += this
        }

        override fun cancel() {
            channelRef
                .getAndChange {
                    it?.let { ChannelHolder() }
                }
                ?.cancel()
        }

        override fun dispose() {
            channelRef
                .getAndSet(null)
                ?.cancel()

            job.cancel()

            disposables -= this
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            channelRef.value?.channel?.apply {
                trySend(
                    Task(
                        startTime = clock.uptime + delay.coerceAtLeastZero(),
                        period = period.coerceAtLeastZero(),
                        task = task
                    )
                )
            }
        }

        private suspend fun execute(scope: CoroutineScope, task: Task) {
            if (clock.uptime < task.startTime) {
                scope.launch {
                    delayUntilStart(task.startTime)
                    executeAndRepeatIfNeeded(task.period, task.task)
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

        private class ChannelHolder {
            val channel = BroadcastChannel<Task>(Channel.BUFFERED)
            val receiveChannel = channel.openSubscription()

            fun cancel() {
                receiveChannel.cancel()
                channel.cancel()
            }
        }

        private data class Task(
            val startTime: Duration,
            val period: Duration,
            val task: () -> Unit
        )
    }
}
