package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

class MaybeOfEmptyNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestMaybeObserver<Int>? = null
        var observer2: TestMaybeObserver<Int>? = null

        doInBackgroundBlocking {
            observer1 = maybeOfEmpty<Int>().test()
        }
        doInBackgroundBlocking {
            observer2 = maybeOfEmpty<Int>().test()
        }

        requireNotNull(observer1).assertComplete()
        requireNotNull(observer2).assertComplete()
    }
}
