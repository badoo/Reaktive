package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

class MaybeOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestMaybeObserver<Int>? = null
        var observer2: TestMaybeObserver<Int>? = null

        doInBackgroundBlocking {
            observer1 = maybeOfNever<Int>().test()
        }
        doInBackgroundBlocking {
            observer2 = maybeOfNever<Int>().test()
        }

        requireNotNull(observer1).assertSubscribed()
        requireNotNull(observer2).assertSubscribed()
    }
}
