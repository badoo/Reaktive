package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.Scheduler.Executor
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicList
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.plusAssign
import com.badoo.reaktive.utils.atomic.remove
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

    override fun newExecutor(): Executor =
        ExecutorImpl()
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }
}

private class ExecutorImpl : Executor {

    private val taskRefs: AtomicList<StableRef<TaskHolder>> = atomicList()
    private var _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean get() = _isDisposed.value

    override fun dispose() {
        _isDisposed.value = true
        cancel()
    }

    override fun submit(delayMillis: Long, task: () -> Unit) {
        submitRepeating(delayMillis, -1L, task)
    }

    override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
        val taskRef = StableRef.create(TaskHolder(this, periodMillis, task))
        taskRefs += taskRef
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
        if (taskRefs.remove(taskRef)) {
            taskRef.dispose()
        }
    }

    fun submitRepeatingTask(periodMillis: Long, task: () -> Unit) {
        val taskRef = StableRef.create(TaskHolder(this, periodMillis, task))
        taskRefs += taskRef
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
