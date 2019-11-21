package com.badoo.reaktive.benchmarks.jmh.flatmap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class FlatMapFlow {

    @Benchmark
    fun iterable() {
        FlatMapConfig
            .input
            .asFlow()
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        flow {
            FlatMapConfig.input.forEach { emit(it) }
        }
            .also(::run)
    }

    private fun run(input: Flow<Int>) {
        runBlocking {
            input
                .flatMapMerge { input }
                .collect()
        }
    }
}
