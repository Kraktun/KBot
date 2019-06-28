package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.TrackedObjectContainer
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.parsePrice
import com.miche.krak.kBot.utils.removeKeyboard
import com.miche.krak.kBot.utils.sendSimpleListKeyboard
import com.miche.krak.kBot.utils.simpleMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.jsoup.Jsoup

private const val TAG = "TRACK_COMMAND"

/**
 * This is only for testing purposes and must not be used for other reasons.
 * Use the API if you need a tracking command.
 * TODO Use a TrackObject rather than passing single values between commands
 */
class TrackCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/track",
        description = "Track",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )

    val acceptedStores = listOf("Amazon")
    val acceptedAmazonDomains = listOf("cn", "in", "co.jp", "com.sg", "com.tr", "ae", "fr", "de", "it", "nl", "es", "co.uk", "ca", "mx", "com", "com.au", "br").sorted()

    /**
     * First part: ask store
     */
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        sendSimpleListKeyboard(absSender, chat, "Choose the store.", acceptedStores)
        MultiCommandsHandler.insertCommand(user, chat, ManageStore())
    }

    /**
     * Second part: check store and ask domain
     */
    private inner class ManageStore : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedStores.contains(arguments)) {
                simpleMessage(absSender, "Invalid store. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonDomains())
            }
            when (arguments) {
                acceptedStores[0] -> {
                    sendSimpleListKeyboard(absSender, chat, "Choose the domain.", acceptedAmazonDomains)
                    MultiCommandsHandler.insertCommand(user, chat, ManageAmazonDomains(), arguments)
                }
            }
        }
    }

    /**
     * Third part: check domain and ask item ID
     */
    private inner class ManageAmazonDomains : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedAmazonDomains.contains(arguments)) {
                simpleMessage(absSender, "Invalid code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonDomains())
            }
            removeKeyboard(absSender, chat, "Send the ID of the item.")
            MultiCommandsHandler.insertCommand(user, chat, ManageAmazonArticle(), arguments)
        }
    }

    /**
     * Fourth part: check id and ask target price
     */
    private inner class ManageAmazonArticle : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val articleId = arguments
            if (articleId.length != 10) {
                simpleMessage(absSender, "Wrong code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonArticle(), data)
            } else {
                simpleMessage(absSender, "Send the target price.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonPrice(), Pair((data as String), articleId))
            }
        }

    }

    /**
     * Fifth part: check price and insert in db
     */
    private inner class ManageAmazonPrice : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val domain = (data as Pair<*, *>).first as String
            val articleId = data.second as String
            val priceD = parsePrice(arguments) ?: 0f
            if (priceD <= 0f) {
                simpleMessage(absSender, "Wrong number. Retry. Use format x,xxx.xx", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonPrice(), data)
            } else {
                GlobalScope.launch {
                    simpleMessage(absSender, "Retrieving current prices for domain $domain and id $articleId", chat)
                    val priceList = getAmazonPrice(domain, articleId)
                    simpleMessage(absSender, priceList.toString(), chat)
                    DatabaseManager.addTrackedObject(
                        userIdK = user.id,
                        objectIdK = articleId,
                        storeK = acceptedStores[0],
                        targetPriceK = priceD,
                        domainK = domain
                    )
                }
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