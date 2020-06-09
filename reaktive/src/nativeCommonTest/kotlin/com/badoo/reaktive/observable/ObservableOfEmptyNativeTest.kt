package com.badoo.reaktive.observable

import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test

class ObservableOfEmptyNativeTest {

    @Test
    fun works_from_background_threads() {
        val observer1 = AtomicReference<TestObservableObserver<Int>?>(null)
        val observer2 = AtomicReference<TestObservableObserver<Int>?>(null)

        doInBackgroundBlocking {
            observer1.value = observableOfEmpty<Int>().test()
        }
        doInBackgroundBlocking {
            observer2.value = observableOfEmpty<Int>().test()
        }

        observer1.value!!.assertComplete()
        observer2.value!!.assertComplete()
    }
}
