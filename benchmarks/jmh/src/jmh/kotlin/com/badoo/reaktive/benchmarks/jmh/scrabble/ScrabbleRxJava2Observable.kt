/*
 * Copyright 2016-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 *
 * Original: https://github.com/Kotlin/kotlinx.coroutines/tree/master/benchmarks/src/jmh
 */

package com.badoo.reaktive.benchmarks.jmh.scrabble

import io.reactivex.Observable
import io.reactivex.Maybe
import io.reactivex.Single
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.TreeMap
import kotlin.collections.Map.Entry
import kotlin.math.max

@State(Scope.Benchmark)
open class ScrabbleRxJava2Observable : ScrabbleBase() {

    @Benchmark
    override fun play(): List<Entry<Int, List<String>>> {


        // Function to compute the score of a given word
        val scoreOfALetter = { letter: Int -> Observable.just(letterScores[letter - 'a'.toInt()]) }

        // score of the same letters in a word
        val letterScore = { entry: Entry<Int, LongWrapper> ->
            Observable.just(
                letterScores[entry.key - 'a'.toInt()] * Integer.min(
                    entry.value.get().toInt(),
                    scrabbleAvailableLetters[entry.key - 'a'.toInt()]
                )
            )
        }

        val toIntegerObservable = { string: String -> Observable.fromIterable(IterableSpliterator.of(string.chars().boxed().spliterator())) }

        // Histogram of the letters in a given word
        val histoOfLetters = { word: String ->
            toIntegerObservable(word)
                .collect(
                    { HashMap<Int, LongWrapper>() },
                    { map: HashMap<Int, LongWrapper>, value: Int ->
                        var newValue: LongWrapper? = map[value]
                        if (newValue == null) {
                            newValue = LongWrapper.zero()
                        }
                        map[value] = newValue.incAndSet()
                    }

                )
        }

        // number of blanks for a given letter
        val blank = { entry: Entry<Int, LongWrapper> ->
            Observable.just(
                max(0L, entry.value.get() - scrabbleAvailableLetters[entry.key - 'a'.toInt()])
            )
        }

        // number of blanks for a given word
        val nBlanks = { word: String ->
            histoOfLetters(word)
                .flatMapObservable { map -> Observable.fromIterable(map.entries) }
                .flatMap(blank)
                .reduce { a, b -> a + b }
        }


        // can a word be written with 2 blanks?
        val checkBlanks = { word: String ->
            nBlanks(word)
                .flatMap { l -> Maybe.just(l <= 2L) }
        }

        // score taking blanks into account letterScore1
        val score2 = { word: String ->
            histoOfLetters(word)
                .flatMapObservable { map -> Observable.fromIterable(map.entries) }
                .flatMap(letterScore)
                .reduce { a, b -> a + b }
        }

        // Placing the word on the board
        // Building the streams of first and last letters
        val first3 = { word: String -> Observable.fromIterable(IterableSpliterator.of(word.chars().boxed().limit(3).spliterator())) }
        val last3 = { word: String -> Observable.fromIterable(IterableSpliterator.of(word.chars().boxed().skip(3).spliterator())) }


        // Stream to be maxed
        val toBeMaxed = { word: String ->
            Observable.just(first3(word), last3(word))
                .flatMap { it }
        }

        // Bonus for double letter
        val bonusForDoubleLetter = { word: String ->
            toBeMaxed(word)
                .flatMap(scoreOfALetter)
                .reduce { a, b -> max(a, b) }
        }

        // score of the word put on the board
        val score3 = { word: String ->
            Maybe.merge(
                listOf(
                    score2(word), score2(word),
                    bonusForDoubleLetter(word),
                    bonusForDoubleLetter(word),
                    Maybe.just(if (word.length == 7) 50 else 0)
                )
            )
                .reduce { a, b -> a + b }
        }

        val buildHistoOnScore: (((String) -> Maybe<Int>) -> Single<TreeMap<Int, List<String>>>) = { score ->
            Observable.fromIterable(shakespeareWords)
                .filter { scrabbleWords.contains(it) }
                .filter { word -> checkBlanks(word).blockingGet() }
                .collect(
                    { TreeMap<Int, List<String>>(Collections.reverseOrder()) },
                    { map: TreeMap<Int, List<String>>, word: String ->
                        val key = score(word).blockingGet()
                        var list = map[key] as MutableList<String>?
                        if (list == null) {
                            list = ArrayList()
                            map[key] = list
                        }
                        list.add(word)
                    }
                )
        }

        // best key / value pairs
        return buildHistoOnScore(score3)
            .flatMapObservable { map -> Observable.fromIterable(map.entries) }
            .take(3)
            .collect(
                { ArrayList<Entry<Int, List<String>>>() },
                { list, entry -> list.add(entry) }
            )
            .blockingGet()
    }
}
