package com.kraktun.kbot.services.tracking

import com.beust.klaxon.Klaxon
import com.kraktun.kbot.commands.core.callbacks.CallbackHolder
import com.kraktun.kbot.commands.core.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.core.MultiCommandInterface
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.tracking.TrackedObject
import com.kraktun.kbot.objects.tracking.TrackedObjectContainerAmazon
import com.kraktun.kbot.utils.*
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

    private val acceptedAmazonDomains = listOf("co.jp", "fr", "de", "it", "nl", "es", "co.uk", "ca", "com").sorted() // not all the domains as they'd be too many
    private val trackedObject = TrackedObject.getEmpty()
    private val companionData = AmazonData(forceSellerK = false, forceShippingK = false)
    private val TAG = "AMAZON_SERVICE"

    override fun getName(): String {
        return "Amazon"
    }

    override fun startTracking(absSender: AbsSender, user: User, chat: Chat) {
        trackedObject.user = user.id
        trackedObject.store = getName()
        sendKeyboard(absSender, chat, "Choose the domain.", getSimpleListKeyboard(acceptedAmazonDomains))
        MultiCommandsHandler.insertCommand(absSender, user, chat, ManageDomains())
    }

    /**
     * Third part: check domain and ask item ID
     */
    private inner class ManageDomains : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedAmazonDomains.contains(arguments)) {
                simpleMessage(absSender, "Invalid code. Retry.", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageDomains())
            } else {
                removeKeyboard(absSender, chat, "Send the ID of the item.")
                trackedObject.extraKey = arguments
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageArticle())
            }
        }
    }

    /**
     * Fourth part: check id, send current prices and ask target price
     */
    private inner class ManageArticle : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (arguments.length != 10) {
                simpleMessage(absSender, "Wrong code. Retry.", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageArticle())
            } else {
                trackedObject.objectId = arguments
                GlobalScope.launch {
                    simpleMessage(absSender, "Retrieving current prices for domain ${trackedObject.extraKey} and id ${trackedObject.objectId}", chat)
                    val priceList = getPrice(trackedObject.extraKey, trackedObject.objectId)
                    simpleMessage(absSender, priceList.toString(), chat)
                    // Send options to enable forced seller or shipment
                    val soldByAmazon = ManageSoldByAmazon()
                    val shippedByAmazon = ManageShippedByAmazon()
                    CallbackProcessor.insertCallback(user.id, soldByAmazon)
                    CallbackProcessor.insertCallback(user.id, shippedByAmazon)
                    val keyboard = InlineKeyboardMarkup()
                    keyboard.keyboard.add(listOf(soldByAmazon.getButton(), shippedByAmazon.getButton()))
                    sendKeyboard(absSender, chat, "Choose the options you need from below.", keyboard)
                    simpleMessage(absSender, "When you are ready send the target price in the form XXX.XX", chat)
                    MultiCommandsHandler.insertCommand(absSender, user, chat, ManagePrice(), listOf(soldByAmazon.getId(), shippedByAmazon.getId())) // Send as data the id of the callbacks, to remove them later
                }
            }
        }
    }

    /**
     * Process click on button Sold by Amazon
     */
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

        override fun processCallback(absSender: AbsSender, callback: CallbackQuery) {
            companionData.forceSellerK = !companionData.forceSellerK
            printlnK(TAG, "Received callback ${getId()}")
            val answer = AnswerCallbackQuery()
            answer.callbackQueryId = callback.id
            val result = if (companionData.forceSellerK) "enabled" else "disabled"
            answer.text = "Confirmed option: $result sold by Amazon."
            executeMethod(absSender = absSender, m = answer)
        }

        override fun getLabel(): String {
            return "Sold by Amazon"
        }
    }

    /**
     * Process click on button Shipped by Amazon
     */
    private inner class ManageShippedByAmazon : CallbackHolder {

        override fun getButton(): InlineKeyboardButton {
            val keyboardButton = InlineKeyboardButton()
            keyboardButton.text = getLabel()
            keyboardButton.callbackData = getId()
            return keyboardButton
        }

        override fun getId(): String {
            return "AmazonService_shippedByAmazon_toggle"
        }

        override fun processCallback(absSender: AbsSender, callback: CallbackQuery) {
            companionData.forceShippingK = !companionData.forceShippingK
            printlnK(TAG, "Received callback ${getId()}")
            val answer = AnswerCallbackQuery()
            answer.callbackQueryId = callback.id
            val result = if (companionData.forceShippingK) "enabled" else "disabled"
            answer.text = "Confirmed option: $result shipped by Amazon."
            executeMethod(absSender = absSender, m = answer)
        }

        override fun getLabel(): String {
            return "Shipped by Amazon"
        }
    }

    /**
     * Fifth part: set target price and ask name
     */
    private inner class ManagePrice : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            (data as List<*>).forEach {
                CallbackProcessor.removeCallback(user.id, it as String)
            }
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
     * Sixth part: set name and save in DB
     */
    private inner class ManageObjectName : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (arguments.isEmpty()) {
                simpleMessage(absSender, "Name can't be empty", chat)
                MultiCommandsHandler.insertCommand(absSender, user, chat, ManageObjectName())
            } else {
                trackedObject.name = arguments
                trackedObject.data = Klaxon().toJsonString(companionData)
                DatabaseManager.addTrackedObject(trackedObject)
                simpleMessage(absSender, "Object saved.", chat)
            }
        }
    }

    companion object {

        const val amazonSellerTag = "Amazon"

        /**
         * Get a list of first $maxIndex prices from amazon (shorter list if there are less)
         * maxIndex is the number of listing to look for
         * maxDepth is the number of attempts to search for maxIndex elements before giving up (if there are 3 listings, but maxIndex is 5, maxDepth becomes the new limit to the loop)
         * Be aware that sometimes if some listings are removed\used their child count is still there, but they don't count to maxIndex, so maxDepth may be a limit before maxIndex even when there are > maxIndex listings
         */
        fun getPrice(domain: String, articleId: String, maxIndex: Int = 5, maxDepth: Int = 20): List<TrackedObjectContainerAmazon> {
            val list = mutableListOf<TrackedObjectContainerAmazon>()
            val doc =
                Jsoup.connect("https://www.amazon.$domain/gp/offer-listing/$articleId/ref=olp_f_used?ie=UTF8&f_new=true") // only where status = new
                    .userAgent("Chrome/75.0.3770").get()
            var child = 1
            while (list.size < maxIndex && child < maxDepth) { // child constrict if there are less than maxIndex prices
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
                            seller =
                                amazonSellerTag // as Amazon uses an image, when we can't find any text, suppose it's amazon
                    val shippedByAmazon =
                        doc.select("#olpOfferList > div > div > div:nth-child($child) > div.a-column.a-span2.olpPriceColumn > span.supersaver > i")
                            .isNotEmpty() // if present is the "prime" logo
                    list.add(
                        TrackedObjectContainerAmazon(
                            priceA = price,
                            sellerA = seller,
                            shippingPriceA = shippingPrice,
                            shippedByAmazon = shippedByAmazon
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

        /**
         * Get prices for object and filter results according to chosen options.
         * Return best price if at least one passes all filters, null otherwise.
         */
        fun filterPrices(obj: TrackedObject): TrackedObjectContainerAmazon? {
            var list = getPrice(domain = obj.extraKey, articleId = obj.objectId)
            val data = Klaxon().parse<AmazonData>(obj.data)
            // filter if only sold by amazon
            if (data != null && data.forceSellerK) list = list.filter { it.seller == amazonSellerTag }
            // filter if only shipped by amazon
            if (data != null && data.forceShippingK) list = list.filter { it.shippedByAmazon }
            return if (list.isNotEmpty()) list.first() else null
        }
    }
}

data class AmazonData(var forceSellerK: Boolean, var forceShippingK: Boolean)