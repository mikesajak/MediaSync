package org.mikesajak.mediasync.app

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import jcifs.netbios.NbtAddress
import jcifs.smb.SmbFile
import kotlinx.android.synthetic.main.activity_smb_share_select.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
* Created by mike on 05.01.16.
*/
class SmbShareSelectActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    val adapter by lazy { HostListViewAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smb_share_select)
        discoveryListView.setAdapter(adapter)

        adapter.setSelectionListener { share ->
            val intent = Intent(this, SmbShareBrowserActivity::class.java)
            intent.putExtra("dir", share)
            startActivity(intent)
        }

        discoveryProgressBar.visibility = View.VISIBLE
        adapter.clear()

        async() {
            try {
                val domains = SmbFile("smb://").list()?.toList()
                Log.d(TAG, "domains: $domains")

                val domains2HostsMap =
                    domains?.toMap { domain ->
                        val hosts = SmbFile("smb://$domain").list()?.toList() ?: ArrayList()

                        val hostSharesList = hosts.map { host ->
                            val shares = SmbFile("smb://$host").list()?.toList() ?: ArrayList()
                            Pair(host, shares.joinToString())
                        }
                        Pair(domain, hostSharesList)
                    }

                val hostInfoList =
                    domains?.flatMap { domain ->
                        val hosts = SmbFile("smb://$domain").list()?.toList() ?: ArrayList()

                        hosts.map { host ->
                            val shares = SmbFile("smb://$host").list { dir, name -> !name.endsWith("$") }?.toList() ?: ArrayList()
                            HostInfo(host, domain, shares)
                        }
                    } ?: ArrayList()

                Log.d(TAG, "----- hosts: $domains2HostsMap")

                uiThread {
                    if (domains2HostsMap == null) adapter.clear()
                    else {
                        adapter.setHosts(hostInfoList)
                    }

                    discoveryProgressBar.visibility = View.INVISIBLE
                }
            } catch (e: Throwable) {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                Log.e(TAG, "******************* Exception: ${e.message}\nStack trace:\n$sw")
                uiThread {
                    Toast.makeText(this@SmbShareSelectActivity, "Error while searching local network: $e: ${e.message}",
                            Toast.LENGTH_LONG).show()
                    discoveryProgressBar.visibility = View.INVISIBLE
                }
            }
        }
    }
}