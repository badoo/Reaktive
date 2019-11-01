/*
 * Copyright 2016-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 *
 * Original: https://github.com/Kotlin/kotlinx.coroutines/tree/master/benchmarks/src/jmh
 */

package com.badoo.reaktive.benchmarks.jmh.scrabble

import java.util.Spliterator
import java.util.Spliterators

object IterableSpliterator {
    @JvmStatic
    fun <T> of(spliterator: Spliterator<T>): Iterable<T> = Iterable { Spliterators.iterator(spliterator) }
}
