package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class MaybeOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        val observer1 = AtomicReference<TestMaybeObserver<Int>?>(null)
        val observer2 = AtomicReference<TestMaybeObserver<Int>?>(null)

        doInBackgroundBlocking {
            observer1.value = maybeOfNever<Int>().test()
        }
        doInBackgroundBlocking {
            observer2.value = maybeOfNever<Int>().test()
        }

        observer1.value!!.assertSubscribed()
        observer2.value!!.assertSubscribed()
    }
}
