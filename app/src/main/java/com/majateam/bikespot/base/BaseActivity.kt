package com.majateam.bikespot.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.majateam.bikespot.helper.InternetConnectionType.*

abstract class BaseActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        registerInternetCheckReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private fun registerInternetCheckReceiver() {
        val internetFilter = IntentFilter()
        internetFilter.addAction(BROADCAST_EVENT_WIFI_STATE_CHANGE)
        internetFilter.addAction(BROADCAST_EVENT_CONNECTIVITY_STATE_CHANGE)
        registerReceiver(broadcastReceiver, internetFilter)
    }

    protected abstract fun setSnackBarMessage()
    protected abstract fun onNetworkConnectionUpdated(isConnected: Boolean)

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setSnackBarMessage()
            onNetworkConnectionUpdated(getConnectivityStatus(applicationContext))
        }
    }

    companion object {
        private const val BROADCAST_EVENT_WIFI_STATE_CHANGE = "android.net.wifi.STATE_CHANGE"
        private const val BROADCAST_EVENT_CONNECTIVITY_STATE_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"

        fun getConnectivityStatus(context: Context): Boolean {
            var result = false
            val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }
    }
}