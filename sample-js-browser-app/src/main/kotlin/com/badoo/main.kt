package com.badoo

import com.badoo.reaktive.samplemppmodule.Counter
import com.badoo.reaktive.samplemppmodule.Counter.Event
import com.badoo.reaktive.observable.subscribe
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun main() {
    document.addEventListener(type = "DOMContentLoaded", callback = { onLoaded() })
}

private fun onLoaded() {
    console.log("Loaded")
    val counter = Counter()

    val valueText = document.getElementById("value")  as HTMLElement
    val loader = document.getElementById("loader")  as HTMLElement
    val incrementButton = document.getElementById("button-increment")  as HTMLElement
    val decrementButton = document.getElementById("button-decrement")  as HTMLElement
    val resetButton = document.getElementById("button-reset") as HTMLElement
    val fibonacciButton = document.getElementById("button-fibonacci")  as HTMLElement

    counter.state.subscribe { state ->
        valueText.innerHTML = state.value.toString()
        loader.hidden = !state.isLoading
    }

    incrementButton.onclick = { counter.onEvent(Event.Increment) }
    decrementButton.onclick = { counter.onEvent(Event.Decrement) }
    resetButton.onclick = { counter.onEvent(Event.Reset) }
    fibonacciButton.onclick = { counter.onEvent(Event.Fibonacci) }
}
