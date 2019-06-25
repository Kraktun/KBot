package com.miche.krak.kBot.objects

class TrackedObject (val user : Int, val objectId : String, val store : String, val domain : String,
                     val targetPrice : Float, forceSellerK : Boolean = false, forceShippingK : Boolean = false)