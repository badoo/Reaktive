package com.badoo.reaktive.scheduler

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

internal class ThreadFactoryImpl(
    private val namePrefix: String
) : ThreadFactory {

    override fun newThread(runnable: Runnable): Thread =
        factory
            .newThread(runnable)
            .apply {
                name = "$namePrefix, $name"
            }

    private companion object {
        private val factory by lazy { Executors.defaultThreadFactory() }
    }
}