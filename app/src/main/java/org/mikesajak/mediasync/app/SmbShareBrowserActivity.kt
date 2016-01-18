package org.mikesajak.mediasync.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import jcifs.smb.SmbFile
import kotlinx.android.synthetic.main.activity_smb_share_browser.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.async
import org.jetbrains.anko.error
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by mike on 07.01.16.
 */
class SmbShareBrowserActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smb_share_browser)

        progressBar.visibility = View.VISIBLE

        val intent = getIntent()
        val dirName = intent?.getStringExtra("dir")
        if (dirName == null) error("Non-empty share name expected")

        val parentDirName = intent?.getStringExtra("parentDir")

        pathTextView.text = ""
        curDirTextView.text = dirName

        async() {
            try {
                val dir = SmbFile("smb://$dirName")
                val parentDir = if (parentDirName != null) SmbFile("smb://$parentDirName") else null
                val dirContents = dir.listFiles { file -> file.isDirectory }?.toList() ?: ArrayList()
                uiThread {
                    directoryListView.adapter = SmbDirListAdapter(this@SmbShareBrowserActivity, parentDir, dirContents,
                            { smbFile ->
                                if (smbFile == parentDir) {
                                    onBackPressed() // simulate back to avoid mess in activity stack
                                } else {
                                    val intent = Intent(this@SmbShareBrowserActivity, SmbShareBrowserActivity::class.java)
                                    intent.putExtra("dir", "$dirName/${smbFile.name}/")
                                    intent.putExtra("parentDir", "$dirName/")
                                    startActivity(intent)
                                }
                            })
                    progressBar.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
                val msg = "Error ocurred while reading remote directory: $dirName."
                error(msg, e)
                uiThread {
                    Toast.makeText(this@SmbShareBrowserActivity, msg, Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }
}