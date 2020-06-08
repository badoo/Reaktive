package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class CompletableOfEmptyNativeTest {

    @Test
    fun works_from_background_threads() {
        val observer1 = AtomicReference<TestCompletableObserver?>(null)
        val observer2 = AtomicReference<TestCompletableObserver?>(null)

        doInBackgroundBlocking {
            observer1.value = completableOfEmpty().test()
        }
        doInBackgroundBlocking {
            observer2.value = completableOfEmpty().test()
        }

        observer1.value!!.assertComplete()
        observer2.value!!.assertComplete()
    }
}
