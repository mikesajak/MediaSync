package org.mikesajak.mediasync.app

import android.app.IntentService
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import org.jetbrains.anko.wifiManager

/**
 * Created by mike on 15.01.16.
 */
class MediaSyncService : IntentService("MediaSyncService"), AnkoLogger {
    companion object {
        val ACTION_SYNC = "org.mikesajak.mediasync.app.action.SYNC"
        val ACTION_BAZ = "org.mikesajak.mediasync.app.action.BAZ"

        val EXTRA_PARAM1 = "org.mikesajak.mediasync.app.extra.PARAM1"
        val EXTRA_PARAM2 = "org.mikesajak.mediasync.app.extra.PARAM2"

        fun startActionSync(context: Context) {
            val intent = Intent(context, MediaSyncService::class.java)
            intent.setAction(ACTION_SYNC)
            context.startService(intent)
        }

        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MediaSyncService::class.java)
            intent.setAction(ACTION_BAZ)
            intent.putExtra(EXTRA_PARAM1, param1)
            intent.putExtra(EXTRA_PARAM2, param2)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                ACTION_SYNC -> handleActionSync()
                ACTION_BAZ -> {
                    val param1 = intent.getStringExtra(EXTRA_PARAM1)
                    val param2 = intent.getStringExtra(EXTRA_PARAM2)
                    handleActionBaz(param1, param2)
                }
            }

        }
    }

    private fun handleActionSync() {
        debug("Running sync action")
        if (wifiManager.isWifiEnabled) {
            val connectionInfo = wifiManager.connectionInfo
            info("Connection info: $connectionInfo")
        }
    }

    private fun handleActionBaz(param1: String, param2: String) {
        throw UnsupportedOperationException("Not yet implemented")
    }

}
