package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.doInBackgroundBlocking
import kotlin.test.Test

class CompletableOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestCompletableObserver? = null
        var observer2: TestCompletableObserver? = null

        doInBackgroundBlocking {
            observer1 = completableOfNever().test()
        }
        doInBackgroundBlocking {
            observer2 = completableOfNever().test()
        }

        requireNotNull(observer1).assertSubscribed()
        requireNotNull(observer2).assertSubscribed()
    }
}
