package com.kraktun.kbot.services.tracking

import com.kraktun.kbot.commands.core.MultiCommandInterface
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.tracking.TrackedObject
import com.kraktun.kbot.objects.tracking.TrackedObjectContainer
import com.kraktun.kbot.utils.parsePrice
import com.kraktun.kbot.utils.removeKeyboard
import com.kraktun.kbot.utils.simpleMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * TODO DOC
 */
class UnieuroService : TrackingInterface {

    private val trackedObject = TrackedObject.getEmpty()
    private val TAG = "UNIEURO_SERVICE"

    override fun getName(): String {
        return "Unieuro"
    }

    override fun startTracking(absSender: AbsSender, user: User, chat: Chat) {
        trackedObject.user = user.id
        trackedObject.store = getName()
        removeKeyboard(absSender, chat, "Send the link for the product")
        MultiCommandsHandler.insertCommand(absSender, user, chat, ManageUrl())
    }

    /**
     *
     */
    private inner class ManageUrl : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            trackedObject.objectId = arguments // url
            GlobalScope.launch {
                simpleMessage(absSender, "Retrieving current price for url ${trackedObject.objectId}", chat)
                val price = getPrice(trackedObject.objectId)
                if (price == null) {
                    simpleMessage(absSender, "Wrong url, retry.", chat)
                    MultiCommandsHandler.insertCommand(absSender, user, chat, ManageUrl())
                } else {
                    simpleMessage(absSender, price.toString(), chat)
                    simpleMessage(absSender, "Send the target price in the form XXX.XX", chat)
                    MultiCommandsHandler.insertCommand(absSender, user, chat, ManagePrice())
                }
            }
        }
    }

    /**
     *
     */
    private inner class ManagePrice : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val priceD = parsePrice(arguments) ?: 0f
            if (priceD <= 0f) {
                simpleMessage(absSender, "Wrong number. Retry.", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManagePrice())
            } else {
                trackedObject.targetPrice = priceD
                simpleMessage(absSender, "Send the name for this object.", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageObjectName())
            }
        }
    }

    /**
     *
     */
    private inner class ManageObjectName : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (arguments.isEmpty()) {
                simpleMessage(absSender, "Name can't be empty", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageObjectName())
            } else {
                trackedObject.name = arguments
                DatabaseManager.addTrackedObject(trackedObject)
                simpleMessage(absSender, "Object saved.", chat)
            }
        }
    }

    companion object {

        fun getPrice(url: String): TrackedObjectContainer? {
            val doc = Jsoup.connect(url).userAgent("Chrome/75.0.3770").get()
            val integer = doc.select("#features > div.details-table > section.container-right-cell > div > div > span.integer").first()?.text() ?: ""
            val decimal = doc.select("#features > div.details-table > section.container-right-cell > div > div > span.decimal").first()?.text() ?: ""
            val shipping = doc.select("#features > div.details-table > section.container-right-cell > article.shipping-container > div:nth-child(2) > div.item-text > span > span").first()?.text() ?: ""
            val price = parsePrice("$integer$decimal")
            val shippingPrice = parsePrice(shipping) ?: ""
            if (price == null) return null
            return TrackedObjectContainer(
                price = price.toString(),
                seller = "Unieuro",
                shippingPrice = shippingPrice.toString()
            )
        }
    }
}