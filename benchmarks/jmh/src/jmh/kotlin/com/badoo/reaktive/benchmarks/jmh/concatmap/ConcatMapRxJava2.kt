package com.badoo.reaktive.benchmarks.jmh.concatmap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapRxJava2 {

    @Benchmark
    fun iterable() {
        Observable
            .fromIterable(ConcatMapConfig.input)
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        Observable.create<Int> { emitter ->
            ConcatMapConfig.input.forEach(emitter::onNext)
            emitter.onComplete()
        }
            .also(::run)
    }

    private fun run(input: Observable<Int>) {
        input
            .concatMap { input }
            .subscribe()
    }
}
