package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class ObservableOfNeverNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestObservableObserver<Int>? = null
        var observer2: TestObservableObserver<Int>? = null

        doInBackgroundBlocking {
            observer1 = observableOfNever<Int>().test()
        }
        doInBackgroundBlocking {
            observer2 = observableOfNever<Int>().test()
        }

        requireNotNull(observer1).assertSubscribed()
        requireNotNull(observer2).assertSubscribed()
    }
}
