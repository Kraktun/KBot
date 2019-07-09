package com.miche.krak.kBot.objects.tracking

/**
 * Object containing a request for an article.
 */
class TrackedObject(
    var name: String,
    var user: Int,
    var objectId: String,
    var store: String,
    var extraKey: String,
    var targetPrice: Float,
    var data: String = "" // Encoded data (JSON)
) {

    companion object {
        fun getEmpty(): TrackedObject {
            return TrackedObject("", 0, "", "", "", 0f)
        }
    }
}