package com.miche.krak.kBot.utils

import org.telegram.telegrambots.meta.api.objects.User

/**
 * Get identification string for user:
 * if it has a username, use that, if not use first + last name or only first
 */
fun getQualifiedUser(user: User) : String {
    return when {
        user.userName != null -> "@${user.userName}"
        user.lastName != null -> "${user.firstName} ${user.lastName}"
        else -> user.firstName
    }
}