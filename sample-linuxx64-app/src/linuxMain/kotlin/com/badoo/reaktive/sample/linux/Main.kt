package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.completable.blockingAwait
import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.samplemppmodule.Counter

/**
 * Use the following Gradle tasks to run the application:
 *
 * - `:runReleaseExecutableLinux` - release mode
 * - `:runDebugExecutableLinux` - debug mode
 */
fun main() {
    val counter = Counter()

    counter.state
        .doOnBeforeNext { state ->
            onStateChanged(
                state = state,
                onEvent = counter::onEvent,
                onExit = counter::dispose,
            )
        }
        .asCompletable()
        .blockingAwait()
}

private fun onStateChanged(
    state: Counter.State,
    onEvent: (Counter.Event) -> Unit,
    onExit: () -> Unit,
) {
    println(state)

    if (!state.isLoading) {
        print("Enter command (+, -, 0, f, q): ")

        when (readln()) {
            "+" -> onEvent(Counter.Event.Increment)
            "-" -> onEvent(Counter.Event.Decrement)
            "0" -> onEvent(Counter.Event.Reset)
            "f" -> onEvent(Counter.Event.Fibonacci)
            "q" -> onExit()

            else -> {
                println("Invalid command")
                onExit()
            }
        }
    }
}
