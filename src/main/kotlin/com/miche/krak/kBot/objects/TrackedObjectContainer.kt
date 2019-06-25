package com.miche.krak.kBot.objects

import java.util.regex.Pattern

data class TrackedObjectContainer (val price : String, val seller : String, val shippingPrice : String, val shippedByAmazon : Boolean) {

    fun totalPrice() : Float {
        val p = Pattern.compile("\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})") //From https://stackoverflow.com/a/32052651
        var m = p.matcher(price.replace(",","."))
        val priceD  = if (m.find()) m.group().toFloat() else 0f
        m = p.matcher(shippingPrice.replace(",","."))
        val shippingPriceD = if (m.find()) m.group().toFloat() else 0f
        return priceD + shippingPriceD
    }

    override fun toString() : String {
        return "Price: $price\nShipping price: $shippingPrice\nSeller: $seller\nShipped by Amazon: $shippedByAmazon\n"
    }
}