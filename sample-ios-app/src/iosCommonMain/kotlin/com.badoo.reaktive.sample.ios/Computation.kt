import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.Single
import platform.Foundation.NSOperationQueue
import platform.posix.sleep

fun calculate(): Single<List<Int>> =
    observableOf(0, 1, 2, 3, 4)
        .subscribeOn(computationScheduler)
        .doOnBeforeNext { println("Should be background thread: ${!isMainThread()}") }
        .map {
            // some off-thread computations
            sleep(1)
            it
        }
        .observeOn(mainScheduler)
        .doOnBeforeNext { println("Should be main thread: ${isMainThread()}") }
        .toList()

private fun isMainThread() = NSOperationQueue.mainQueue == NSOperationQueue.currentQueue
