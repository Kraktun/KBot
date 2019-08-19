package com.kraktun.kbot.jobs

import org.telegram.telegrambots.meta.generics.LongPollingBot

data class JobInfo(val name: String, val interval: Int, val trigger: String, val group: String, val delay: Int = 30, val botList: List<LongPollingBot> = listOf())