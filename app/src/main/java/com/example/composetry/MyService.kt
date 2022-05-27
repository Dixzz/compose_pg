package com.example.composetry

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val NOTIFICATION_ID = 11
const val NOTIFICATION_CHANNEL_ID = "com.example.composetry.NOTIFICATION_CHANNEL_ID"
val ADB_WIFI_ENABLED = "adb_wifi_enabled"

private const val TAG = "MyService"

const val FORCE_STOP_SERVICE = "force_stop_service"

class MyService : Service() {

    private var mAdbReceiver: AdbReceiver? = null
    private lateinit var mSharedPreferences: SharedPreferences

    private var isWirelessDebugEnabled: Boolean
        get() = try {
            Settings.Global.getInt(contentResolver, ADB_WIFI_ENABLED, 0) == 1
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        set(value) {
            try {
                Settings.Global.putInt(contentResolver, ADB_WIFI_ENABLED, if (value) 1 else 0)
            } catch (e: Exception) {
                e.printStackTrace()
                sendNotification(e.message ?: "Unable to set ADB WiFi")
            }
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
        startAdbService(this)
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved")
    }

    override fun onDestroy() {
        startAdbService(this)
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "stop" -> {
                mSharedPreferences.edit().putBoolean(FORCE_STOP_SERVICE, true).apply()
                AdbReceiver.unregister(this, mAdbReceiver)
                stopForeground(true)
            }
            "enable" -> {
                isWirelessDebugEnabled = true
            }
            "disable" -> {
                isWirelessDebugEnabled = false
                startStateNotification()
            }
            "start" -> {
                if (mSharedPreferences.getBoolean(FORCE_STOP_SERVICE, false) || mAdbReceiver == null) {
                    createChannel()
                    registerForegroundService()
                    mAdbReceiver = AdbReceiver.register(this)
                    mSharedPreferences.edit().putBoolean(FORCE_STOP_SERVICE, false).apply()
                    if (isWirelessDebugEnabled) {
                        if (!mSharedPreferences.getBoolean(AdbReceiver.IS_ADB_DIRTY_KEY, true)) {
                            val ip = mSharedPreferences.getString(AdbReceiver.ADB_PORT_IP_KEY, "")
                            val port =
                                mSharedPreferences.getInt(AdbReceiver.ADB_PORT_KEY, -1).toString()
                            if (!ip.isNullOrEmpty() && port.isNotEmpty()) {
                                sendNotificationIP("$ip:$port")
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "onStartCommand: ${intent?.action} $isWirelessDebugEnabled")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    companion object {
        fun startAdbService(context: Context) {
            context.applicationContext.startService(Intent(context.applicationContext, MyService::class.java).apply {
                action = "start"
            })
        }

        fun Context.startStateNotification(): Notification {
            return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Listening to wifi")
                .setSmallIcon(R.drawable.debug).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.priority = NotificationManager.IMPORTANCE_MIN
                    }
                }
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.debug,
                        "Stop",
                        PendingIntent.getService(
                            this,
                            0,
                            Intent(this, MyService::class.java).apply {
                                action = "stop"
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
                        )
                    ).build()
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.debug,
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
                        R.drawable.debug,
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

        infix fun Context.sendNotification(msg: String){
            NotificationManagerCompat.from(this).notify(
                NOTIFICATION_ID,
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Wireless debugging")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.debug)
                    .setAutoCancel(true)
                    .setOngoing(false).addAction(
                        NotificationCompat.Action.Builder(
                            R.drawable.debug,
                            "Stop",
                            PendingIntent.getService(
                                this,
                                0,
                                Intent(this, MyService::class.java).apply {
                                    action = "stop"
                                },
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
                            )
                        ).build()
                    )
                    .addAction(
                        NotificationCompat.Action.Builder(
                            R.drawable.debug,
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
                            R.drawable.debug,
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
                    .build()
            )
        }

        fun Context.sendNotificationIP(ipPort: String) {
            NotificationManagerCompat.from(this).notify(
                NOTIFICATION_ID,
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Wireless debugging")
                    .setContentText("Connected to $ipPort")
                    .setSmallIcon(R.drawable.debug)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .addAction(
                        NotificationCompat.Action.Builder(
                            R.drawable.debug,
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