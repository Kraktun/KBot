package com.kraktun.kbot.jobs

import org.telegram.telegrambots.meta.bots.AbsSender

data class JobInfo(val name: String, val interval: Int, val trigger: String, val group: String, val delay: Int = 30, val botList: List<AbsSender> = listOf())
