package com.kraktun.kbot.jobs

import org.telegram.telegrambots.meta.bots.AbsSender

data class JobInfo(val key: String, var interval: Long, var initialDelay: Long = 30, val botList: List<AbsSender> = listOf())
