package org.mikesajak.mediasync.app;

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_home_wifi_selection.*
import org.jetbrains.anko.*

public class HomeWifiSelectionActivity: AppCompatActivity(), AnkoLogger {
    companion object {
        val SELECT_WIFI_REQUEST = 1
        val WIFI_SELECTION_RESULT = "selectedNetwork"
    }
    val TAG = "HomeWifiSelectionActivity"

    val rescanPeriod = 30000L
    private var wifiReceiver: WifiReceiver? = null

    private val timerHandler = Handler()
    private val timerTask = TimerTask()

    private var state: Parcelable? = null

    private var selectedItem = -1

    inner class TimerTask(): Runnable {
        override fun run() {
            info("timer fired...")
            if (wifiManager.isWifiEnabled) {
                Log.d(TAG, "WiFi enable, starting scan...")
//                debug("WiFi enabled - start scan...")
                onUiThread { progressBar.visibility = View.VISIBLE }
                wifiManager.startScan()
            } else {
//                debug("WiFi disabled - trying to enable wifi")
                Log.d(TAG, "WiFi disabled - skipping scan.")
            }
            timerHandler.postDelayed(this, rescanPeriod)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        debug("onCreate")
        super.onCreate(savedInstanceState);
        if (state != null)
            wifiListView.onRestoreInstanceState(state)

        setContentView(R.layout.activity_home_wifi_selection);

        wifiListView.onItemClick { adapterView, view, position, rowId ->
//            selectButton.enabled = wifiListView.selectedItem != null
            if (position > 0) selectButton.enabled = true
            selectedItem = position
        }

        selectButton.onClick {
            val resultIntent = Intent()
            val adapter = wifiListView.adapter as WifiListAdapter
            val selectedNetwork = adapter.itemAt(selectedItem)
            resultIntent.putExtra(WIFI_SELECTION_RESULT, selectedNetwork.SSID)
            setResult(Activity.RESULT_OK, resultIntent)

            ///////////////
//            getSharedPreferences("select_wifi")



            finish()
        }

        multiSelectionSwitch.onCheckedChange { button, enabled ->
            wifiListView.choiceMode = if (enabled) ListView.CHOICE_MODE_MULTIPLE_MODAL else ListView.CHOICE_MODE_SINGLE
        }
    }

    private fun initAsyncUpdates() {
        if (!wifiManager.isWifiEnabled) {
            AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("WI-FI is disabled. Do you want to enable it?")
                    .setPositiveButton("Yes", { dialog, which ->
                        wifiManager.setWifiEnabled(true)
                        startCyclicRefresh()
                        dialog.dismiss()
                    })
                    .setNegativeButton("No", { dialog, which -> dialog.dismiss() })
                    .create()
                    .show()

        } else startCyclicRefresh()
    }

    private fun startCyclicRefresh() {
        wifiReceiver = wifiReceiver ?: WifiReceiver(wifiManager)
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        timerHandler.postDelayed(timerTask, 0)
    }

    private fun stopAsyncUpdates() {
        unregisterReceiver(wifiReceiver)
        timerHandler.removeCallbacks(timerTask)
    }

    override fun onPause() {
        debug("onPause")
        stopAsyncUpdates()
        super.onPause()
    }

    override fun onResume() {
        debug("onResume")
        initAsyncUpdates()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        state = wifiListView.onSaveInstanceState()
        outState?.putParcelable("listState", state)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        state = savedInstanceState?.getParcelable("listState")
    }

    private fun updateUI(connections: List<ScanResult>) {
        debug("updateUI: connections=$connections")
        val checked = wifiListView.checkedItemPositions
        val selection = (0..checked.size()).filter { checked.get(it) }.map { wifiListView.getItemAtPosition(it) as String }

        val sortedConnections = connections.sortedWith(comparator { s1, s2 ->
            val ssid1 = s1.SSID.toLowerCase()
            val ssid2 = s2.SSID.toLowerCase()
            if (ssid1 != ssid2) ssid1.compareTo(ssid2) else s1.BSSID.compareTo(s2.BSSID)
        })
        wifiListView.adapter = WifiListAdapter(this, sortedConnections)
        selection.forEachIndexed { i, sel ->
            val item = wifiListView.getItemAtPosition(i) as String
            if (item == sel) wifiListView.setItemChecked(i, true)
        }
        progressBar.visibility = View.INVISIBLE
    }

    inner class WifiReceiver(val wifiManager: WifiManager) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connections = wifiManager.scanResults
            updateUI(connections)
        }

    }

}
