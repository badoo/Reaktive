package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class SingleOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestSingleObserver<Int>? = null
        var observer2: TestSingleObserver<Int>? = null

        doInBackgroundBlocking {
            observer1 = singleOfNever<Int>().test()
        }
        doInBackgroundBlocking {
            observer2 = singleOfNever<Int>().test()
        }

        requireNotNull(observer1).assertSubscribed()
        requireNotNull(observer2).assertSubscribed()
    }
}
