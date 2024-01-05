package com.badoo

import com.badoo.reaktive.samplemppmodule.Counter
import com.badoo.reaktive.samplemppmodule.Counter.Event
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

fun main() {
    document.addEventListener(type = "DOMContentLoaded", callback = { onLoaded() })
}

private fun onLoaded() {
    val counter = Counter()

    val valueText = document.getElementById("value") as HTMLElement
    val loader = document.getElementById("loader") as HTMLElement
    val incrementButton = document.getElementById("button-increment") as HTMLElement
    val decrementButton = document.getElementById("button-decrement") as HTMLElement
    val resetButton = document.getElementById("button-reset") as HTMLElement
    val fibonacciButton = document.getElementById("button-fibonacci") as HTMLElement
    val increaseAfter1s = document.getElementById("button-increase-after-1s") as HTMLElement
    val increaseEvery1s = document.getElementById("button-increase-every-1s") as HTMLElement

    counter.state.subscribe { state ->
        valueText.innerHTML = state.value.toString()
        loader.hidden = !state.isLoading
    }

    incrementButton.onclick = { counter.onEvent(Event.Increment) }
    decrementButton.onclick = { counter.onEvent(Event.Decrement) }
    resetButton.onclick = { counter.onEvent(Event.Reset) }
    fibonacciButton.onclick = { counter.onEvent(Event.Fibonacci) }

    increaseAfter1s.onclick = {
        counter.onEvent(Event.IncrementAfter(1.seconds))
    }

    increaseEvery1s.onclick = {
        counter.onEvent(Event.IncrementEvery(1.seconds))
    }
}
