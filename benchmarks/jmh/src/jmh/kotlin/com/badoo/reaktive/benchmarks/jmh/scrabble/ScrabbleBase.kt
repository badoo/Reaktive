/*
 * Copyright 2016-2019 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
 *
 * Original: https://github.com/Kotlin/kotlinx.coroutines/tree/master/benchmarks/src/jmh
 */

package com.badoo.reaktive.benchmarks.jmh.scrabble

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream

@State(Scope.Benchmark)
abstract class ScrabbleBase {
    abstract fun play(): List<Map.Entry<Int, List<String>>>

    abstract class LongWrapper {
        abstract fun get(): Long

        fun incAndSet(): LongWrapper {
            return object : LongWrapper() {
                override fun get(): Long = this@LongWrapper.get() + 1L
            }
        }

        companion object {
            fun zero(): LongWrapper {
                return object : LongWrapper() {
                    override fun get(): Long = 0L
                }
            }
        }
    }

    @JvmField
    val letterScores: IntArray = intArrayOf(1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10)

    @JvmField
    val scrabbleAvailableLetters: IntArray =
        intArrayOf(9, 2, 2, 1, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1)

    @JvmField
    val scrabbleWords: Set<String> = readResource("ospd.txt.gz")

    @JvmField
    val shakespeareWords: Set<String> = readResource("words.shakespeare.txt.gz")

    private fun readResource(path: String) =
        BufferedReader(InputStreamReader(GZIPInputStream(this.javaClass.classLoader.getResourceAsStream(path)))).lines()
            .map { it.lowercase() }.collect(Collectors.toSet())

    init {
        val expected = listOf(
            120 to listOf("jezebel", "quickly"),
            118 to listOf("zephyrs"), 116 to listOf("equinox")
        )
        val actual = play().map { it.key to it.value }
        if (expected != actual) {
            error("Incorrect benchmark, output: $actual")
        }
    }
}
