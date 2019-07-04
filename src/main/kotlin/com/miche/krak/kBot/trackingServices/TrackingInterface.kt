package com.miche.krak.kBot.trackingServices

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Simple interface for tracking services
 */
interface TrackingInterface {

    fun getName(): String

    fun startTracking(absSender: AbsSender, user: User, chat: Chat)
}