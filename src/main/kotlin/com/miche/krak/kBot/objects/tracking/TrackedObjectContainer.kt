package com.miche.krak.kBot.objects.tracking

import com.miche.krak.kBot.utils.parsePrice

/**
 * Object containing a listing (article by seller with price)
 */
open class TrackedObjectContainer(val price: String, val seller: String, val shippingPrice: String) {

    fun totalPrice(): Float {
        val priceD = parsePrice(price) ?: 0f
        val shippingPriceD = parsePrice(shippingPrice) ?: 0f
        return priceD + shippingPriceD
    }

    override fun toString(): String {
        return "Price: $price\nShipping price: $shippingPrice\nSeller: $seller\n"
    }
}