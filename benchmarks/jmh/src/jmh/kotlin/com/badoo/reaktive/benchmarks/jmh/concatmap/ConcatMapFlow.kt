package com.badoo.reaktive.benchmarks.jmh.concatmap

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapFlow {

    @Benchmark
    fun run() {
        runBlocking {
            val input = ConcatMapConfig.input.asFlow()

            input
                .flatMapConcat { input }
                .collect()
        }
    }
}
