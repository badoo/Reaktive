package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.Scheduler.Executor
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import libgtk3.FALSE
import libgtk3.TRUE
import libgtk3.g_source_remove_by_user_data
import libgtk3.g_timeout_add
import libgtk3.gpointer

class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Executor = ExecutorImpl(disposables)

    override fun destroy() {
        disposables.dispose()
    }
}

private class ExecutorImpl(
    private val disposables: CompositeDisposable
) : Executor {

    private val taskRefs: AtomicReference<List<StableRef<TaskHolder>>> = AtomicReference(emptyList())
    private var _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean get() = _isDisposed.value

    init {
        disposables += this
    }

    override fun dispose() {
        _isDisposed.value = true
        cancel()
        disposables -= this
    }

    override fun submit(delayMillis: Long, task: () -> Unit) {
        submitRepeating(delayMillis, -1L, task)
    }

    override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
        val taskRef = StableRef.create(TaskHolder(this, periodMillis, task))
        taskRefs.update { it + taskRef }
        g_timeout_add(startDelayMillis.toUInt(), staticCFunction(::callbackOneShotTask).reinterpret(), taskRef.asCPointer())
    }

    override fun cancel() {
        taskRefs
            .getAndUpdate { emptyList() }
            .forEach {
                g_source_remove_by_user_data(it.asCPointer())
                it.dispose()
            }
    }

    fun removeTaskRef(taskRef: StableRef<TaskHolder>) {
        var removed = false

        taskRefs.update {
            val newList = it - taskRef
            removed = newList.size < it.size
            newList
        }

        if (removed) {
            taskRef.dispose()
        }
    }

    fun submitRepeatingTask(periodMillis: Long, task: () -> Unit) {
        val taskRef = StableRef.create(TaskHolder(this, periodMillis, task))
        taskRefs.update { it + taskRef }
        g_timeout_add(periodMillis.toUInt(), staticCFunction(::callbackRepeating).reinterpret(), taskRef.asCPointer())
    }
}

private class TaskHolder(
    val executor: ExecutorImpl,
    val periodMillis: Long,
    val task: () -> Unit
)

private fun callbackOneShotTask(data: gpointer): Int {
    val stableRef = data.asStableRef<TaskHolder>()
    val holder = stableRef.get()
    holder.executor.removeTaskRef(stableRef)

    if (holder.periodMillis >= 0L) {
        holder.executor.submitRepeatingTask(holder.periodMillis, holder.task)
    }

    holder.task()

    return FALSE
}

private fun callbackRepeating(data: gpointer): Int {
    data.asStableRef<TaskHolder>().get().task()

    return TRUE
}
