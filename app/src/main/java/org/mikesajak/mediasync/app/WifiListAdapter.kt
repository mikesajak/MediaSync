package org.mikesajak.mediasync.app

import android.content.Context
import android.net.wifi.ScanResult
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import org.jetbrains.anko.layoutInflater

/**
 * Created by mike on 16.01.16.
 */
class WifiListAdapter(context: Context, val connections: List<ScanResult>)
        : ArrayAdapter<ScanResult>(context, 0, connections) {

    private val selection = SparseBooleanArray(connections.size)

    internal data class ViewHolder(//val layout: CheckableLinearLayout,
                                   val titleTextView: TextView,
                                   val descrTextView: TextView)

    fun itemAt(pos: Int) = connections[pos]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val conInfo = getItem(position)

        val rowView = getOrCreateView(convertView, parent)
        val holder = rowView.tag as ViewHolder

        holder.titleTextView.text = conInfo.SSID
        holder.descrTextView.text = "(${conInfo.BSSID})"

        return rowView
    }

    private fun getOrCreateView(convertView: View?, parent: ViewGroup): View {
        return  if (convertView != null) convertView
                else {
                    val view = context.layoutInflater.inflate(R.layout.wifi_row, parent, false)
                    val holder = ViewHolder(//view.findViewById(R.id.wifiRowLayout) as CheckableLinearLayout,
                                            view.findViewById(R.id.wifiNameTextView) as TextView,
                                            view.findViewById(R.id.wifiDescriptionTextView) as TextView)
                    view.tag = holder
                    view
                }
    }
}