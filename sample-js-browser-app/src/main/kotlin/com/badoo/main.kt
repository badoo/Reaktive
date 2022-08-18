package com.badoo

import com.badoo.reaktive.samplemppmodule.binder.KittenBinder
import com.badoo.reaktive.samplemppmodule.KittenStoreBuilderImpl
import org.w3c.dom.get
import kotlinx.browser.document

/**
 * How to run: execute "sample-js-browser-app:run" Gradle task
 * and click on the link in build log (like "http://localhost:8080/")
 */

private val kittenBinder = KittenBinder(KittenStoreBuilderImpl())
private var lastVisibility = false

fun main() {
    document.addEventListener(
        "DOMContentLoaded",
        {
            kittenBinder.onViewCreated(KittenViewImpl())
            checkVisibility()
        }
    )

    document.addEventListener("visibilitychange", { checkVisibility() })
}

private fun checkVisibility() {
    val isVisible = !(document["hidden"] as Boolean)
    if (isVisible != lastVisibility) {
        lastVisibility = isVisible
        if (isVisible) {
            kittenBinder.onStart()
        } else {
            kittenBinder.onStop()
        }
    }
}
