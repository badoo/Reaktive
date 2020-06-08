package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class CompletableOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        val observer1 = AtomicReference<TestCompletableObserver?>(null)
        val observer2 = AtomicReference<TestCompletableObserver?>(null)

        doInBackgroundBlocking {
            observer1.value = completableOfNever().test()
        }
        doInBackgroundBlocking {
            observer2.value = completableOfNever().test()
        }

        observer1.value!!.assertSubscribed()
        observer2.value!!.assertSubscribed()
    }
}
