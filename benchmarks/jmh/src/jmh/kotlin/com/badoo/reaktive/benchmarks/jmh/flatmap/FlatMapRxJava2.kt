package com.badoo.reaktive.benchmarks.jmh.flatmap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class FlatMapRxJava2 {

    @Benchmark
    fun iterable() {
        Observable
            .fromIterable(FlatMapConfig.input)
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        Observable.create<Int> { emitter ->
            FlatMapConfig.input.forEach(emitter::onNext)
            emitter.onComplete()
        }
            .also(::run)
    }

    private fun run(input: Observable<Int>) {
        input
            .flatMap { input }
            .subscribe()
    }
}
