package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.doInBackgroundBlocking
import kotlin.test.Test

class CompletableOfEmptyNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestCompletableObserver? = null
        var observer2: TestCompletableObserver? = null

        doInBackgroundBlocking {
            observer1 = completableOfEmpty().test()
        }
        doInBackgroundBlocking {
            observer2 = completableOfEmpty().test()
        }

        requireNotNull(observer1).assertComplete()
        requireNotNull(observer2).assertComplete()
    }
}
