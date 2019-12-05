package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MutableFreezableHelperTest {

    @Test
    fun does_not_create_mutable_WHEN_created() {
        var isCalled = false

        MutableFreezableHelper(
            mutableFactory = {
                isCalled = true
                Mutable()
            },
            freezableFactory = ::Freezable
        )

        assertFalse(isCalled)
    }

    @Test
    fun does_not_create_freezable_WHEN_created() {
        var isCalled = false

        MutableFreezableHelper(
            mutableFactory = ::Mutable,
            freezableFactory = {
                isCalled = true
                Freezable()
            }
        )

        assertFalse(isCalled)
    }

    @Test
    fun creates_mutable_WHEN_not_frozen_and_accessed() {
        val helper = helper()

        val obj = helper.obj

        assertTrue(obj is Mutable)
    }

    @Test
    fun creates_freezable_WHEN_frozen_and_accessed() {
        val helper = helper().freeze()

        val obj = helper.obj

        assertTrue(obj is Freezable)
    }

    @Test
    fun does_not_create_mutable_WHEN_frozen_and_accessed() {
        var isCalled = false

        val helper =
            MutableFreezableHelper(
                mutableFactory = {
                    isCalled = true
                    Mutable()
                },
                freezableFactory = ::Freezable
            )

        helper.freeze()
        helper.obj

        assertFalse(isCalled)
    }

    @Test
    fun object_is_same_WHEN_not_frozen_and_accessed_second_time() {
        val helper = helper()

        val obj1 = helper.obj
        val obj2 = helper.obj

        assertSame(obj1, obj2)
    }

    @Test
    fun object_is_same_WHEN_frozen_and_accessed_second_time() {
        val helper = helper()

        helper.freeze()
        val obj1 = helper.obj
        val obj2 = helper.obj

        assertSame(obj1, obj2)
    }

    @Test
    fun converts_mutable_to_freezable_WHEN_accessed_second_time_after_freeze() {
        val helper = helper()

        val obj1 = helper.obj
        helper.freeze()
        val obj2 = helper.obj

        assertSame<Any?>(obj1, (obj2 as Freezable).mutable)
    }

    private fun helper(): MutableFreezableHelper<Any, Mutable, Freezable> =
        MutableFreezableHelper(mutableFactory = ::Mutable, freezableFactory = ::Freezable)

    private class Mutable

    private class Freezable(val mutable: Mutable? = null)
}
