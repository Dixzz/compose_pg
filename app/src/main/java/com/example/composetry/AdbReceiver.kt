package com.example.composetry

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import com.example.composetry.MyService.Companion.sendNotificationIP
import com.example.composetry.MyService.Companion.startStateNotification


const val WIRELESS_STATUS_CONNECTED = 4
const val WIRELESS_STATUS_DISCONNECTED = 5
const val WIRELESS_DEBUG_PORT_EXTRA = "adb_port"
const val WIRELESS_STATUS_EXTRA = "status"

const val WIRELESS_DEBUG_STATE_CHANGED_ACTION = "com.android.server.adb.WIRELESS_DEBUG_STATUS"

private const val TAG = "AdbReceiver"

class AdbReceiver : BroadcastReceiver() {
    companion object {
        fun register(context: Context): AdbReceiver {
            val receiver = AdbReceiver()
            context.registerReceiver(
                receiver,
                IntentFilter(WIRELESS_DEBUG_STATE_CHANGED_ACTION)
            )
            return receiver
        }
    }

    lateinit var notificationManager: NotificationManager

    private fun getIPAddress(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ip = wifiInfo.ipAddress
        return String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }


    override fun onReceive(context: Context, intent: Intent) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            WIRELESS_DEBUG_STATE_CHANGED_ACTION -> {
                val status = intent.getIntExtra(WIRELESS_STATUS_EXTRA, 0)
                val port = intent.getIntExtra(WIRELESS_DEBUG_PORT_EXTRA, 0)
                when (status) {
                    WIRELESS_STATUS_CONNECTED -> {
                        Log.d(TAG, "Connected to port $port ,${getIPAddress(context)}")
                        context.sendNotificationIP("${getIPAddress(context)}:$port")
                    }
                    WIRELESS_STATUS_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from port $port")
                        notificationManager.notify(NOTIFICATION_ID, context.startStateNotification())
                    }
                }
            }
        }
        Log.d(TAG, "onReceive: ${intent.action}")
    }
}