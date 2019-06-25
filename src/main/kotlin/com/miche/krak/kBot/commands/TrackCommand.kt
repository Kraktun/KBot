package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.TrackedObjectContainer
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.removeKeyboard
import com.miche.krak.kBot.utils.sendSimpleListKeyboard
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.jsoup.Jsoup
import java.util.regex.Pattern

private const val TAG = "TRACK_COMMAND"

/**
 * This is only for testing purposes and must not be used for other reasons.
 * Use the API if you need a tracking command.
 */
class TrackCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/track",
        description = "Track",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )

    val acceptedStores = listOf("Amazon")
    val acceptedDomains = listOf("cn", "in", "co.jp", "com.sg", "com.tr", "ae", "fr", "de", "it", "nl", "es", "co.uk", "ca", "mx", "com", "com.au", "br").sorted()

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        sendSimpleListKeyboard(absSender, chat, "Choose the store.", acceptedStores)
        MultiCommandsHandler.insertCommand(user, chat, ManageStore())
    }

    private inner class ManageStore :MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedStores.contains(arguments)) {
                simpleMessage(absSender, "Invalid store. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonCode())
            }
            when (arguments) {
                acceptedStores[0] -> {
                    sendSimpleListKeyboard(absSender, chat, "Choose the domain.", acceptedDomains)
                    MultiCommandsHandler.insertCommand(user, chat, ManageAmazonCode(), arguments)
                }
            }
        }
    }

    private inner class ManageAmazonCode : MultiCommandInterface {


        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            if (!acceptedDomains.contains(arguments)) {
                simpleMessage(absSender, "Invalid code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonCode())
            }
            removeKeyboard(absSender, chat, "Send the ID of the item.")
            MultiCommandsHandler.insertCommand(user, chat, ManageTargetPrice(), arguments)
        }
    }

    private inner class ManageTargetPrice : MultiCommandInterface {

        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val p = Pattern.compile("\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})") //From https://stackoverflow.com/a/32052651
            val m = p.matcher(arguments.replace(",","."))
            val priceD  = if (m.find()) m.group().toFloat() else 0f
            if (priceD > 0f) {
                removeKeyboard(absSender, chat, "Send the ID of the item.")
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonArticles(), Pair(data as String, priceD))
            } else {
                simpleMessage(absSender, "Invalid number. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageTargetPrice())
            }
        }

    }

    private inner class ManageAmazonArticles : MultiCommandInterface {

        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val domain = (data as Pair<*, *>).first as String
            val targetPrice = data.second as Float
            val articleId : String = arguments
            if (articleId.length != 10) {
                simpleMessage(absSender, "Wrong code. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageAmazonArticles())
            } else {
                val priceList = getPrices(domain, articleId)
                simpleMessage(absSender, priceList.toString(), chat)
                DatabaseManager.addTrackedObject(userIdK = user.id, objectIdK = articleId, storeK = acceptedStores[0], targetPriceK = targetPrice, domainK = domain)
            }
        }
    }

    companion object {

        fun getPrices(domain: String, articleId: String): List<TrackedObjectContainer> {
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