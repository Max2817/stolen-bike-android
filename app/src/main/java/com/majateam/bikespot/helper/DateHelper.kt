package com.majateam.bikespot.helper

import java.util.*

/**
 * Simons project - Human Equation - http://www.equationhumaine.co
 * Created by nmartino on 15-12-18.
 */
object DateHelper {
    fun getDateMonthsAgo(numOfMonthsAgo: Int): Date {
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.MONTH, -1 * numOfMonthsAgo)
        return c.time
    }
}