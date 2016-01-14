package org.mikesajak.mediasync.app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import jcifs.smb.SmbFile
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick

/**
 * Created by mike on 07.01.16.
 */
class SmbDirListAdapter(val context: Context, val parentDir: SmbFile?, val contents: List<SmbFile>,
                        private val clickListener: ((SmbFile) -> Unit)? = null)
        : BaseAdapter() {

    override fun getCount() = contents.size + (if (parentDir != null) 1 else 0)

    override fun getItem(position: Int) =
            if (parentDir != null) {
                if (position == 0) parentDir else contents[position - 1]
            } else contents[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val dir = getItem(position)

        val view0 = view ?: context.layoutInflater.inflate(R.layout.dir_list_row, null)
        val image = view0.findViewById(R.id.imageView)
        val label = view0.findViewById(R.id.textView) as TextView

        label.text = if (dir === parentDir) ".." else dir.name

        if (clickListener == null) {
            image.setOnClickListener(null)
            label.setOnClickListener(null)
        }
        else {
            image.onClick { clickListener?.invoke(dir) }
            label.onClick { clickListener?.invoke(dir) }
        }




        return view0
    }

}
