package com.badoo.reaktive.utils.queue

import kotlin.native.concurrent.freeze

class CopyOnWriteQueueTest : QueueTests by QueueTestsImpl(CopyOnWriteQueue<String?>().freeze())
