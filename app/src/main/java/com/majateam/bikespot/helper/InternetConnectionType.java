package com.majateam.bikespot.helper;

/**
 * Stolen Bike Created by nmartino on 2017-04-17.
 */

public enum InternetConnectionType {

    TYPE_WIFI(10),
    TYPE_MOBILE(11),
    TYPE_NOT_CONNECTED(12);

    private int connectionTypeValue;

    private InternetConnectionType(int connectionTypeValue) {
        this.connectionTypeValue = connectionTypeValue;
    }

    public int getValue(){
        return connectionTypeValue;
    }
}
