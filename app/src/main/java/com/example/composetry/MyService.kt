package com.example.composetry

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

const val NOTIFICATION_ID = 11
const val NOTIFICATION_CHANNEL_ID = "com.example.composetry.NOTIFICATION_CHANNEL_ID"
val ADB_WIFI_ENABLED = "adb_wifi_enabled"

private const val TAG = "MyService"
class MyService : Service() {

    private var mAdbReceiver: AdbReceiver? = null

    private var isWirelessDebugEnabled: Boolean
        get() = try {
            Settings.Global.getInt(contentResolver, ADB_WIFI_ENABLED, 0) == 1
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        set(value) {
            Settings.Global.putInt(contentResolver, ADB_WIFI_ENABLED, if (value) 1 else 0)
        }

    fun registerForegroundService() {
        val notification = startStateNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Service",
                NotificationManager.IMPORTANCE_HIGH
            )
        } else return
        notificationManager.createNotificationChannel(channel)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        ContextCompat.startForegroundService(this, Intent(this, MyService::class.java))
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved")
    }

    override fun onDestroy() {
        ContextCompat.startForegroundService(this, Intent(this, MyService::class.java))
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        registerForegroundService()
        if (mAdbReceiver == null) {
            mAdbReceiver = AdbReceiver.register(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "enable" -> {
                isWirelessDebugEnabled = true
            }
            "disable" -> {
                isWirelessDebugEnabled = false
                startStateNotification()
            }
        }
        Log.d(TAG, "onStartCommand: ${intent?.action} $isWirelessDebugEnabled")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    companion object {
        fun Context.startStateNotification(): Notification {
            return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Listening to wifi")
                .setSmallIcon(R.drawable.ic_launcher_foreground).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.priority = NotificationManager.IMPORTANCE_MIN
                    }
                }
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_launcher_foreground,
                        "Enable",
                        PendingIntent.getService(
                            this,
                            0,
                            Intent(this, MyService::class.java).apply {
                                action = "enable"
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
                        )
                    ).build()
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_launcher_foreground,
                        "Disable",
                        PendingIntent.getService(
                            this,
                            0,
                            Intent(this, MyService::class.java).apply {
                                action = "disable"
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
                        )
                    ).build()
                )
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setOngoing(true)
                .build()
        }

        fun Context.sendNotificationIP(ipPort: String) {
            NotificationManagerCompat.from(this).notify(
                NOTIFICATION_ID,
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Wireless Debugging enabled")
                    .setContentText("Connected to $ipPort")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .addAction(
                        NotificationCompat.Action.Builder(
                            R.drawable.ic_launcher_foreground,
                            "Disable",
                            PendingIntent.getService(
                                this,
                                0,
                                Intent(this, MyService::class.java).apply {
                                    action = "disable"
                                },
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
                            )
                        ).build()
                    )
                    .build()
            )
        }
    }
}