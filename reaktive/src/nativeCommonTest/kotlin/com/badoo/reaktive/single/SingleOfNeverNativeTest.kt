package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class SingleOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        val observer1 = AtomicReference<TestSingleObserver<Int>?>(null)
        val observer2 = AtomicReference<TestSingleObserver<Int>?>(null)

        doInBackgroundBlocking {
            observer1.value = singleOfNever<Int>().test()
        }
        doInBackgroundBlocking {
            observer2.value = singleOfNever<Int>().test()
        }

        observer1.value!!.assertSubscribed()
        observer2.value!!.assertSubscribed()
    }
}
