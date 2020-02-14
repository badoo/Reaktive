package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.clock.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext

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
                .getAndUpdate {
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

        override fun submit(delayMillis: Long, task: () -> Unit) {
            submitRepeating(delayMillis, -1L, task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            channelRef.value?.channel?.apply {
                offer(
                    Task(
                        startAtMillis = clock.uptimeMillis + startDelayMillis,
                        periodMillis = periodMillis,
                        task = task
                    )
                )
            }
        }

        private suspend fun execute(scope: CoroutineScope, task: Task) {
            if (clock.uptimeMillis < task.startAtMillis) {
                scope.launch {
                    delayUntilStart(task.startAtMillis)
                    repeatTask(task.periodMillis, task.task)
                }
            } else {
                repeatTask(task.periodMillis, task.task)
            }
        }

        private suspend fun repeatTask(periodMillis: Long, task: () -> Unit) {
            while (true) {
                val nextStartAtMillis = clock.uptimeMillis + periodMillis

                mutex.lock()
                try {
                    task()
                } finally {
                    mutex.unlock()
                }

                if (periodMillis < 0) {
                    break
                }

                delayUntilStart(nextStartAtMillis)
            }
        }

        private suspend fun delayUntilStart(startAtMillis: Long) {
            val uptimeMillis = clock.uptimeMillis
            if (uptimeMillis < startAtMillis) {
                delay(startAtMillis - uptimeMillis)
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
            val startAtMillis: Long,
            val periodMillis: Long,
            val task: () -> Unit
        )
    }
}
