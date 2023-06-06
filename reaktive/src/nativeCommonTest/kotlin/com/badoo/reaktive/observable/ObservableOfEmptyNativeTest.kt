package com.badoo.reaktive.observable

import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class ObservableOfEmptyNativeTest {

    @Test
    fun works_from_background_threads() {
        var observer1: TestObservableObserver<Int>? = null
        var observer2: TestObservableObserver<Int>? = null

        doInBackgroundBlocking {
            observer1 = observableOfEmpty<Int>().test()
        }
        doInBackgroundBlocking {
            observer2 = observableOfEmpty<Int>().test()
        }

        requireNotNull(observer1).assertComplete()
        requireNotNull(observer2).assertComplete()
    }
}
