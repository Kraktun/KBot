package com.kraktun.kbot.jobs

import kotlinx.coroutines.CoroutineScope

abstract class JobTask {

    abstract fun execute(scope: CoroutineScope)
}
