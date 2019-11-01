package com.badoo.reaktive.benchmarks.jmh.scrabble

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.blockingGet
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.merge
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.collect
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.reduce
import com.badoo.reaktive.observable.take
import com.badoo.reaktive.observable.toList
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.single.flatMapObservable
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.Collections
import java.util.HashMap
import java.util.TreeMap
import kotlin.math.max

@State(Scope.Benchmark)
open class ScrabbleReaktive : ScrabbleBase() {

    @Benchmark
    override fun play(): List<MutableMap.MutableEntry<Int, List<String>>> {
        val scoreOfALetter = { letter: Int ->
            observableOf(letterScores[letter - 'a'.toInt()])
        }

        val letterScore = { entry: Map.Entry<Int, LongWrapper> ->
            observableOf(
                letterScores[entry.key - 'a'.toInt()] * Integer.min(
                    entry.value.get().toInt(),
                    scrabbleAvailableLetters[entry.key - 'a'.toInt()]
                )
            )
        }

        val toIntegerStream = { string: String ->
            IterableSpliterator.of(string.chars().boxed().spliterator()).asObservable()
        }

        val histoOfLetters: (String) -> Single<HashMap<Int, LongWrapper>> = { word: String ->
            toIntegerStream(word).collect(HashMap<Int, LongWrapper>()) { accumulator, value ->
                var newValue: LongWrapper? = accumulator[value]
                if (newValue == null) {
                    newValue = LongWrapper.zero()
                }
                accumulator[value] = newValue.incAndSet()
                accumulator
            }
        }

        val blank = { entry: Map.Entry<Int, LongWrapper> ->
            observableOf(max(0L, entry.value.get() - scrabbleAvailableLetters[entry.key - 'a'.toInt()]))
        }

        val nBlanks = { word: String ->
            histoOfLetters(word)
                .flatMapObservable { map ->
                    map.entries.asObservable()
                }
                .flatMap(blank)
                .reduce { a, b -> a + b }
        }

        val checkBlanks = { word: String ->
            nBlanks(word).flatMap { l ->
                maybeOf(l <= 2L)
            }
        }

        val score2 = { word: String ->
            histoOfLetters(word)
                .flatMapObservable { map ->
                    map.entries.asObservable()
                }
                .flatMap(letterScore)
                .reduce { a, b -> a + b }
        }

        val first3 = { word: String ->
            IterableSpliterator.of(word.chars().boxed().limit(3).spliterator()).asObservable()
        }

        val last3 = { word: String ->
            IterableSpliterator.of(word.chars().boxed().skip(3).spliterator()).asObservable()
        }

        val toBeMaxed = { word: String ->
            observableOf(first3(word), last3(word)).flatMap { it }
        }

        // Bonus for double letter
        val bonusForDoubleLetter = { word: String ->
            toBeMaxed(word)
                .flatMap(scoreOfALetter)
                .reduce(::max)
        }

        var i = 0
        val score3 = { word: String ->
            i++
            listOf(
                score2(word),
                score2(word),
                bonusForDoubleLetter(word),
                bonusForDoubleLetter(word),
                maybeOf(if (word.length == 7) 50 else 0)
            )
                .merge()
                .reduce { a, b -> a + b }
        }

        val buildHistoOnScore: (((String) -> Maybe<Int>) -> Single<TreeMap<Int, List<String>>>) = { score ->
            shakespeareWords
                .asObservable()
                .filter { scrabbleWords.contains(it) && checkBlanks(it).blockingGet()!! }
                .collect(TreeMap<Int, List<String>>(Collections.reverseOrder())) { acc, value ->
                    val key = score(value).blockingGet()!!
                    var list = acc[key] as MutableList<String>?
                    if (list == null) {
                        list = ArrayList()
                        acc[key] = list
                    }
                    list.add(value)
                    acc
                }
        }

        return buildHistoOnScore(score3)
            .flatMapObservable { map ->
                map.entries.asObservable()
            }
            .take(3)
            .toList()
            .blockingGet()
    }
}
