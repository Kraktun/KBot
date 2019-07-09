package com.miche.krak.kBot.objects.tracking

/**
 * Object containing a listing (article by seller with price)
 */
class TrackedObjectContainerAmazon(private val priceA: String, private val sellerA: String, private val shippingPriceA: String, val shippedByAmazon: Boolean) : TrackedObjectContainer(priceA, sellerA, shippingPriceA) {

    override fun toString(): String {
        return "Price: $priceA\nShipping price: $shippingPriceA\nSeller: $sellerA\nShipped by Amazon: $shippedByAmazon\n"
    }
}