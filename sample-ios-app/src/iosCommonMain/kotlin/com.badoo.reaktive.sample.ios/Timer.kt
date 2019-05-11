import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.Single
import platform.Foundation.NSOperationQueue

fun calculateFibonacci(): Single<List<Pair<Int, Int>>> =
    observableOf(37, 38, 39, 40, 41)
        .subscribeOn(computationScheduler)
        .doOnBeforeNext { println("Should be background thread: ${!isMainThread()}") }
        .map {
            // some off-thread computations
            Pair(it, fibonacci(it))
        }
        .observeOn(mainScheduler)
        .doOnBeforeNext { println("Should be main thread: ${isMainThread()}") }
        .toList()

private fun fibonacci(n: Int): Int =
    when (n) {
        0 -> 0
        1 -> 1
        else -> fibonacci(n - 1) + fibonacci(n - 2)
    }

private fun isMainThread() = NSOperationQueue.mainQueue == NSOperationQueue.currentQueue
