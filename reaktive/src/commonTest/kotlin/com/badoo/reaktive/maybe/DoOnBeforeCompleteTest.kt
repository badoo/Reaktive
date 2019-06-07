package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeCompleteTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeComplete {} }) {

    @Test
    fun calls_action_before_completion() {
        val upstream = TestMaybe<Nothing>()
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Nothing> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }
}