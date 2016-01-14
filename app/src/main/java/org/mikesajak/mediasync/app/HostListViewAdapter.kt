package org.mikesajak.mediasync.app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import org.jetbrains.anko.layoutInflater
import java.util.*

class HostListViewAdapter(context: Context) : BaseExpandableListAdapter() {

    private val inflater by lazy { context.layoutInflater }

    private val hosts = ArrayList<HostInfo>()

    private var clickListener: ((String) -> Unit)? = null

    fun clear() {
        hosts.clear()
        notifyDataSetChanged()
    }

//    fun addHost(domain: String, host: HostInfo) {
//        val groupItem = itemList.find { it.name == domain } ?: HostInfo(domain, ArrayList())
//        val subItemIdx = groupItem.subItems.indexOfFirst { it.name == host.name }
//        if (subItemIdx == -1) {
//            groupItem.subItems.add(host)
//        } else {
//            groupItem.subItems.removeAt(subItemIdx)
//            groupItem.subItems.add(subItemIdx, host)
//        }
//
//        notifyDataSetChanged()
//    }

    fun setHosts(hostList: List<HostInfo>) {
        hosts.clear()
        hosts.addAll(hostList)

        notifyDataSetChanged()
    }

    fun setSelectionListener(listener: ((String) -> Unit)?) {
        clickListener = listener
        notifyDataSetChanged()
    }

    override fun getChild(groupPos: Int, childPos: Int) =
            hosts[groupPos].shares[childPos]

    override fun getChildId(groupPos: Int, childPos: Int) =
            childPos.toLong()

    override fun getChildrenCount(groupPos: Int) =
            hosts[groupPos].shares.size

    override fun getChildView(groupPos: Int, childPos: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        val host = getGroup(groupPos)
        val share = getChild(groupPos, childPos)
        val view = convertView ?: inflater.inflate(R.layout.child_row_title_description, null)

        val titleLabel = view.findViewById(R.id.childRowTitleTextView) as TextView
        titleLabel.text = share
        if (clickListener == null) titleLabel.setOnClickListener(null)
        else titleLabel.setOnClickListener{ clickListener?.invoke("${host.name}/$share/") }

        val descriptionLabel = view.findViewById(R.id.childRowDescriptionTextView) as TextView
//        val description = item.shares.joinToString()
//        descriptionLabel.text = if (description.length < 200) description else description.substring(0, 200) + "..."
        descriptionLabel.text = null
        descriptionLabel.visibility = View.GONE
        return view
    }

    override fun getGroup(groupPos: Int) =
            hosts[groupPos]

    override fun getGroupCount() =
            hosts.size

    override fun getGroupId(groupPos: Int) =
            groupPos.toLong()

    override fun getGroupView(groupPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {
        val hostInfo = getGroup(groupPos)

        val view = convertView ?: inflater.inflate(R.layout.main_row_title_description, null)
        val titleLabel = view.findViewById(R.id.mainRowTitleTextView) as TextView
        titleLabel.text= hostInfo.name

        val descrLabel = view.findViewById(R.id.mainRowDescriptionTextView) as TextView
        descrLabel.text = hostInfo.workgroup

        val imageView = view.findViewById(R.id.imageView)
        imageView.visibility = View.GONE

        return view
    }

    override fun hasStableIds() = true

    override fun isChildSelectable(groupPos: Int, childPos: Int) = true
}