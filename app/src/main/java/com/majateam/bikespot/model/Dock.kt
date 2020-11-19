package com.majateam.bikespot.model

import io.realm.RealmObject

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
open class Dock : RealmObject() {
    var lat: String? = null
    var lng: String? = null
}