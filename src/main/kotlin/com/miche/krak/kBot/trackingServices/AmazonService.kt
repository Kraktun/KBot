package com.miche.krak.kBot.trackingServices

import com.miche.krak.kBot.commands.core.CallbackHolder
import com.miche.krak.kBot.commands.core.CallbackProcessor
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.TrackedObject
import com.miche.krak.kBot.objects.TrackedObjectContainer
import com.miche.krak.kBot.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

class AmazonService : TrackingInterface {

    private val acceptedAmazonDomains = listOf("cn", "in", "co.jp", "com.sg", "com.tr", "ae", "fr", "de", "it", "nl", "es", "co.uk", "ca", "mx", "com", "com.au", "br").sorted()
    private val trackedObject = TrackedObject.getEmpty()
    private val TAG = "AMAZON_SERVICE"

    override fun getName(): String {
        return "Amazon"
    }

    override fun startTracking(absSender: AbsSender, user: User, chat: Chat) {
        trackedObject.user = user.id
        trackedObject.store = getName()
        sendSimpleListKeyboard(absSender, chat, "Choose the domain.", acceptedAmazonDomains)
        MultiCommandsHandler.insertCommand(user, chat, ManageDomains())
    }

    /**
     * Third part: check domain and ask item ID
     */
    private inner class ManageDomains : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedAmazonDomains.contains(arguments)) {
                simpleMessage(absSender, "Invalid code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageDomains())
            } else {
                removeKeyboard(absSender, chat, "Send the ID of the item.")
                trackedObject.domain = arguments
                MultiCommandsHandler.insertCommand(user, chat, ManageArticle())
            }
        }
    }

    /**
     * Fourth part: check id, send current prices and ask target price
     */
    private inner class ManageArticle : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val articleId = arguments
            if (articleId.length != 10) {
                simpleMessage(absSender, "Wrong code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageArticle())
            } else {
                trackedObject.objectId = arguments
                GlobalScope.launch {
                    simpleMessage(absSender, "Retrieving current prices for domain ${trackedObject.domain} and id ${trackedObject.objectId}", chat)
                    val priceList = getAmazonPrice(trackedObject.domain, trackedObject.objectId)
                    simpleMessage(absSender, priceList.toString(), chat)
                    val soldByAmazon = ManageSoldByAmazon()
                    CallbackProcessor.insertCallback(soldByAmazon)
                    val keyboard = InlineKeyboardMarkup()
                    keyboard.keyboard.add(listOf(soldByAmazon.getButton()))
                    sendKeyboard(absSender, chat, "Choose the options you need from below.\n\nWhen you are ready send the target price.", keyboard)
                    MultiCommandsHandler.insertCommand(user, chat, ManagePrice(), listOf(soldByAmazon.getId()))
                }
            }
        }
    }

    private inner class ManageSoldByAmazon : CallbackHolder {

        override fun getButton(): InlineKeyboardButton {
            val keyboardButton = InlineKeyboardButton()
            keyboardButton.text = getLabel()
            keyboardButton.callbackData = getId()
            return keyboardButton
        }

        override fun getId(): String {
            return "AmazonService_soldByAmazon_toggle"
        }

        override fun processCallback(absSender: AbsSender, callback : CallbackQuery) {
            trackedObject.forceSellerK = true
            printlnK(TAG, "Received callback ${getId()}")
            val answer = AnswerCallbackQuery()
            answer.callbackQueryId = callback.id
            answer.text = "Confirmed option."
            executeMethod(absSender = absSender, m = answer)
        }

        override fun getLabel(): String {
            return "Sold by Amazon"
        }
    }

    /**
     * Fifth part: set target price and ask name
     */
    private inner class ManagePrice : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            CallbackProcessor.removeCallback((data as List<*>)[0] as String)
            val priceD = parsePrice(arguments) ?: 0f
            if (priceD <= 0f) {
                simpleMessage(absSender, "Wrong number. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManagePrice())
            } else {
                trackedObject.targetPrice = priceD
                simpleMessage(absSender, "Send the name for this object.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageObjectName())
            }
        }
    }

    /**
     * Sixth part: set name and save in DB
     */
    private inner class ManageObjectName : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (arguments.isEmpty()) {
                simpleMessage(absSender, "Name can't be empty", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageObjectName())
            } else {
                trackedObject.name = arguments
                DatabaseManager.addTrackedObject(trackedObject)
                simpleMessage(absSender, "Object saved.", chat)
            }
        }
    }

    companion object {

        /**
         * Get a list of first 5 prices from amazon (shorter list if there are less)
         */
        fun getAmazonPrice(domain: String, articleId: String): List<TrackedObjectContainer> {
            val list = mutableListOf<TrackedObjectContainer>()
            val doc =
                Jsoup.connect("https://www.amazon.$domain/gp/offer-listing/$articleId/ref=olp_f_used?ie=UTF8&f_new=true")
                    .userAgent("Chrome/75.0.3770").get()
            var child = 1
            while (list.size < 5 && child < 20) { //child constrict if there are less than 5 prices
                val price =
                    doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpPriceColumn > span.a-size-large.a-color-price.olpOfferPrice.a-text-bold")
                        .first()?.text()
                if (price != null && price.isNotEmpty()) {
                    var shippingPrice =
                        doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpPriceColumn > p > span > span.olpShippingPrice").first()?.text()
                            ?: ""
                    if (shippingPrice.isEmpty()) shippingPrice = "0.00"
                    var seller =
                        doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpSellerColumn > h3").first()?.text()
                            ?: ""
                    if (seller.isEmpty())
                        if (doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpSellerColumn > h3 > img").isNotEmpty())
                            seller = "Amazon"
                    val shipping =
                        doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpPriceColumn > span.supersaver > i")
                            .isNotEmpty()
                    list.add(
                        TrackedObjectContainer(
                            price = price,
                            seller = seller,
                            shippingPrice = shippingPrice,
                            shippedByAmazon = shipping
                        )
                    )
                }
                child++
            }
            return list.sortedWith(Comparator { a, b ->
                when {
                    a.totalPrice() > b.totalPrice() -> 1
                    a.totalPrice() < b.totalPrice() -> -1
                    else -> 0
                }
            })
        }
    }
}