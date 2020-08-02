package com.badoo.reaktive.utils.queue

import kotlin.native.concurrent.freeze

class LinkedFreezableQueueTest : QueueTests by QueueTestsImpl(LinkedFreezableQueue<String?>().freeze())
