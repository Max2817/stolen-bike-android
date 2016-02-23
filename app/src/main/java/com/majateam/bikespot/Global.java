package com.majateam.bikespot;

/**
 * Created by Nicolas Martino on 10/06/15.
 */
public class Global {

    private static final Boolean STAGING = false;
    public static final String ENDPOINT = "http://api.velocalisateur.com/";
    private static final String STAGING_URL = "staging/";
    public static final String BIKES_URL = ((STAGING) ? STAGING_URL : "") + "bikes";
    public static final String DOCKS_URL = ((STAGING) ? STAGING_URL : "") + "docks";
}
