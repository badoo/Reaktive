package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.freeze

class CopyOnWriteQueueTest : QueueTests by QueueTestsImpl(CopyOnWriteQueue<String?>().freeze())
