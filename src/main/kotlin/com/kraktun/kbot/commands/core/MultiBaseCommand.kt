package com.kraktun.kbot.commands.core

import java.time.Instant

class MultiBaseCommand(val multiInterface: MultiCommandInterface, val data: Any?, val time: Instant = Instant.now())