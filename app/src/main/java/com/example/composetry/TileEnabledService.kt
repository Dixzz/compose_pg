package com.example.composetry

import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.composetry.MyService.Companion.sendNotification

@RequiresApi(Build.VERSION_CODES.N)
class TileEnabledService : TileService() {

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

    override fun onStartListening() {
        super.onStartListening()
        updateQsTile()
    }

    override fun onClick() {
        super.onClick()
        isWirelessDebugEnabled = !isWirelessDebugEnabled
        MyService.startAdbService(this)
        updateQsTile()
    }


    private fun updateQsTile() {
        qsTile.state = if (isWirelessDebugEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}