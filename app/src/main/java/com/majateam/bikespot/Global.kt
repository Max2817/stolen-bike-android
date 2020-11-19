package com.majateam.bikespot

/**
 * Created by Nicolas Martino on 10/06/15.
 */
object Global {
    private const val STAGING = false
    const val ENDPOINT = "http://api.velocalisateur.com/"
    private const val STAGING_URL = "staging/"
    val BIKES_URL = (if (STAGING) STAGING_URL else "") + "bikes"
    val DOCKS_URL = (if (STAGING) STAGING_URL else "") + "docks"
}