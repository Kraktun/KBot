package com.kraktun.kbot.commands.core

import java.time.Instant

class MultiBaseCommand(
    val multiInterface: MultiCommandInterface,
    val data: Any?, // data from previous message
    val time: Instant = Instant.now(), // instant the message was received
    val TTL: Long = 60L,
) // TimeToLive
