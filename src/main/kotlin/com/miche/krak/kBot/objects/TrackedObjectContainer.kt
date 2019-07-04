package com.miche.krak.kBot.objects

import com.miche.krak.kBot.utils.parsePrice

/**
 * Object containing a listing (article by seller with price)
 */
data class TrackedObjectContainer(val price: String, val seller: String, val shippingPrice: String, val shippedByAmazon: Boolean) {

    fun totalPrice(): Float {
        val priceD = parsePrice(price) ?: 0f
        val shippingPriceD = parsePrice(shippingPrice) ?: 0f
        return priceD + shippingPriceD
    }

    override fun toString(): String {
        return "Price: $price\nShipping price: $shippingPrice\nSeller: $seller\nShipped by Amazon: $shippedByAmazon\n"
    }
}