package org.mikesajak.mediasync.app

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by mike on 08.01.16.
 */
class MainActivity : AppCompatActivity() {

    private var serviceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        actionBar.menu
//        setSupportActionBar(toolbar)

//        startButton.onClick { MediaSyncService.startActionSync(this@MainActivity) }
        startButton.onClick {
//            startActivity<HomeWifiSelectionActivity>()
//            startActivity<NavigationDrawerActivity>()
            startActivity<TabbedActivity>()
        }

        fab.setOnClickListener {
            fab.setImageResource(if (!serviceRunning) android.R.drawable.ic_media_play
                                 else android.R.drawable.ic_media_pause)
            serviceRunning = !serviceRunning
            statusValue.text = if (!serviceRunning) "STOPPED" else "STARTED"
        }

        Snackbar.make(mainCoordinatorLayout, "Service status: STOPPED", Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity<SettingsActivity>()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
