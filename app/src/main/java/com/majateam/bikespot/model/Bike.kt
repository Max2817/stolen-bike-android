package com.majateam.bikespot.model

import android.text.format.DateFormat
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
open class Bike : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var lat: String? = null
    var lng: String? = null
    var date: Long = 0
    var title: String? = null
    var description: String? = null
    var brand: String? = null
    val convertedDate: String
        get() {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = date * 1000
            val format: String
            format = if (Locale.getDefault().toString().split("_")
                        .toTypedArray()[0] == Locale.FRENCH.toString()) {
                "dd/MM/yyyy"
            } else {
                "MM-dd-yyyy"
            }
            return DateFormat.format(format, cal).toString()
        }
}