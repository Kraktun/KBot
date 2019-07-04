package com.miche.krak.kBot.objects

/**
 * Object containing a request for an article
 */
class TrackedObject(
    var name: String,
    var user: Int,
    var objectId: String,
    var store: String,
    var domain: String,
    var targetPrice: Float,
    var forceSellerK: Boolean = false,
    var forceShippingK: Boolean = false
) {

    companion object {
        fun getEmpty(): TrackedObject {
            return TrackedObject("", 0, "", "", "", 0f)
        }
    }
}