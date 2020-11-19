package com.majateam.bikespot.helper

/**
 * Simons project - Human Equation - http://www.equationhumaine.co
 * Created by nmartino on 15-12-16.
 */
object MapHelper {
    /**
     *
     * @param lat1 Latitude of the First Location
     * @param lng1 Logitude of the First Location
     * @param lat2 Latitude of the Second Location
     * @param lng2 Longitude of the Second Location
     * @return distance between two lat-lon in float format
     */
    fun distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {
        val earthRadius = 3958.75
        val dLat = Math.toRadians(lat2 - lat1.toDouble())
        val dLng = Math.toRadians(lng2 - lng1.toDouble())
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val dist = earthRadius * c
        val meterConversion = 1609
        return (dist * meterConversion).toFloat()
    }
}