package org.mikesajak.mediasync.app

import android.content.Context
import android.net.wifi.ScanResult
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import org.jetbrains.anko.layoutInflater

/**
 * Created by mike on 16.01.16.
 */
class WifiListAdapter(context: Context, connections: List<ScanResult>) : ArrayAdapter<ScanResult>(context, 0, connections) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val conInfo = getItem(position)

        val view = convertView ?: context.layoutInflater.inflate(R.layout.wifi_row, parent, false)
        val titleLabel = view.findViewById(R.id.wifiNameTextView) as TextView
        titleLabel.text = conInfo.SSID

        val descrLabel = view.findViewById(R.id.wifiDescriptionTextView) as TextView
        descrLabel.text = "(${conInfo.BSSID})"

        val rowLayout = view.findViewById(R.id.wifiRowLayout) as CheckableLinearLayout
//        rowLayout.

        return view
    }

    class ItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            throw UnsupportedOperationException()
        }

    }
}